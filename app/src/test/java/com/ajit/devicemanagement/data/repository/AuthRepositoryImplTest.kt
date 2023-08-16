package com.ajit.devicemanagement.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.ajit.devicemanagement.data.model.User
import com.ajit.devicemanagement.util.FireStoreCollection
import com.ajit.devicemanagement.util.SharedPrefConstants
import com.ajit.devicemanagement.util.UiState
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P], manifest = Config.NONE)
class AuthRepositoryImplTest {
    private lateinit var authRepository: AuthRepositoryImpl
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var appPreferences: SharedPreferences
    private lateinit var gson: Gson
    private lateinit var storage: FirebaseStorage


    @Before
    fun setup() {
        ShadowLog.stream = System.out // Print logs to console

        FirebaseAppInitializer.initializeFirebaseApp()

        // Initialize necessary dependencies
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        appPreferences = ApplicationProvider.getApplicationContext<Context>()
            .getSharedPreferences("test_prefs", Context.MODE_PRIVATE)
        gson = Gson()
        storage = FirebaseStorage.getInstance()

        // Create the AuthRepositoryImpl instance with mocked dependencies
        authRepository = AuthRepositoryImpl(auth, database, appPreferences, gson, storage)
    }
    // ...

    @Test
    fun testRegisterUser_Success() {
        // Arrange
        val email = "test@example.com"
        val password = "password"
        val user = User("John Doe")
        var result: UiState<String>? = null

        // Act
        authRepository.registerUser(email, password, user) { state ->
            result = state
        }

        // Get the FirebaseUser from FirebaseAuth for verification
        val firebaseUser = auth.currentUser

        // Get the user snapshot from the Firebase Firestore for verification
        val userDocument = database.collection(FireStoreCollection.USER).document(user.id)
        var userSnapshot: DocumentSnapshot? = null
        userDocument.get().addOnSuccessListener { snapshot ->
            userSnapshot = snapshot
        }

        // Get the stored user from shared preferences for verification
        val storedUserStr = appPreferences.getString(SharedPrefConstants.USER_SESSION, null)
        val storedUser = gson.fromJson(storedUserStr, User::class.java)

        // Assert
        assertEquals(UiState.Success("User register successfully!"), result)
        assertNotNull(firebaseUser)
        assertEquals(email, firebaseUser?.email)
        assertEquals(user.id, firebaseUser?.uid)
        assertNotNull(userSnapshot)
        assertEquals(user, userSnapshot?.toObject(User::class.java))
        assertEquals(user, storedUser)
    }

}
