package com.ajit.devicemanagement.data.repository

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.ajit.devicemanagement.data.model.User
import com.ajit.devicemanagement.util.FireStoreCollection
import com.ajit.devicemanagement.util.SharedPrefConstants
import com.ajit.devicemanagement.util.UiState
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import java.io.FileOutputStream


class AuthRepositoryImpl(
    val auth: FirebaseAuth,
    val database: FirebaseFirestore,
    val appPreferences: SharedPreferences,
    val gson: Gson,
) : AuthRepository {

    private val storageRef = FirebaseStorage.getInstance().reference




        override fun registerUser(
            email: String,
            password: String,
            user: User, result: (UiState<String>) -> Unit
        ) {
            auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        user.id = it.result.user?.uid ?: ""
                        updateUserInfo(user) { state ->
                            when(state){
                                is UiState.Success -> {
                                    storeSession(id = it.result.user?.uid ?: "") {
                                        if (it == null){
                                            result.invoke(UiState.Failure("User register successfully but session failed to store"))
                                        }else{
                                            result.invoke(
                                                UiState.Success("User register successfully!")
                                            )
                                        }
                                    }
                                }
                                is UiState.Failure -> {
                                    result.invoke(UiState.Failure(state.error))
                                }
                                else -> {}
                            }
                        }
                    }else{
                        try {
                            throw it.exception ?: java.lang.Exception("Invalid authentication")
                        } catch (e: FirebaseAuthWeakPasswordException) {
                            result.invoke(UiState.Failure("Authentication failed, Password should be at least 6 characters"))
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            result.invoke(UiState.Failure("Authentication failed, Invalid email entered"))
                        } catch (e: FirebaseAuthUserCollisionException) {
                            result.invoke(UiState.Failure("Authentication failed, Email already registered."))
                        } catch (e: Exception) {
                            result.invoke(UiState.Failure(e.message))
                        }
                    }
                }
                .addOnFailureListener {
                    result.invoke(
                        UiState.Failure(
                            it.localizedMessage
                        )
                    )
                }
        }

    override fun updateUserInfo(user: User, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.USER).document(user.id)
        document
            .set(user)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("User has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun loginUser(
        email: String,
        password: String,
        result: (UiState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Log.e("login","login success ${task.result}")
                    storeSession(id = task.result.user?.uid ?: ""){
                        if (it == null){
                            result.invoke(UiState.Failure("Failed to store local session"))
                        }else{


                            result.invoke(UiState.Success("Login successfully!"))
                        }
                    }
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Authentication failed, Check email and password"))
            }
    }

    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    result.invoke(UiState.Success("Email has been sent"))

                } else {
                    result.invoke(UiState.Failure(task.exception?.message))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Authentication failed, Check email"))
            }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        appPreferences.edit().putString(SharedPrefConstants.USER_SESSION,null).apply()


        result.invoke()
    }

    override fun updateUser(user: User, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.USER).document(user.id)
        document.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    document.update(
                        "name", user.name,
                        "mobileNumber", user.mobileNumber,
                        "updatedAt", System.currentTimeMillis()
                    )
                        .addOnSuccessListener {
                            // Update the user details in shared preferences
                            storeSession(user.id) { storedUser ->
                                if (storedUser != null) {
                                    result.invoke(UiState.Success("User has been updated successfully"))
                                } else {
                                    result.invoke(UiState.Failure("Failed to update user in shared preferences"))
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            result.invoke(UiState.Failure(exception.localizedMessage))
                        }
                } else {
                    result.invoke(UiState.Failure("User does not exist"))
                }
            }
            .addOnFailureListener { exception ->
                result.invoke(UiState.Failure(exception.localizedMessage))
            }
    }


    override fun storeSession(id: String, result: (User?) -> Unit) {
        database.collection(FireStoreCollection.USER).document(id)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful){
                    val user = it.result.toObject(User::class.java)
                    appPreferences.edit().putString(SharedPrefConstants.USER_SESSION,gson.toJson(user)).apply()
                    result.invoke(user)
                }else{
                    result.invoke(null)
                }
            }
            .addOnFailureListener {
                result.invoke(null)
            }
    }


    override fun uploadProfilePicture(userId: String, imageUri: Uri, result: (UiState<String>) -> Unit) {
        val profilePicRef = storageRef.child("profilePicture/$userId")
        val uploadTask = profilePicRef.putFile(imageUri)

        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                profilePicRef.downloadUrl.addOnSuccessListener { uri ->
                    val userProfile = hashMapOf(
                        "profilePicture" to uri.toString()
                    )


                    database.collection("user").document(userId)
                        .set(userProfile, SetOptions.merge())
                        .addOnSuccessListener {
                            result.invoke(UiState.Success("Profile updated"))
                        }
                        .addOnFailureListener { e ->
                            result.invoke(UiState.Failure("Profile not updated"))
                        }

                }
            } else {
                result.invoke(UiState.Failure("Something went wrong"))
            }
        }
    }




    override fun getSession(result: (User?) -> Unit) {
        val user_str = appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
        if (user_str == null) {
            result.invoke(null)
        } else {
            val user = gson.fromJson(user_str, User::class.java)
            result.invoke(user)
        }
    }




    }


