package com.ajit.devicemanagement.data.repository

import android.util.Log
import com.ajit.devicemanagement.data.model.RoomDevice
import com.ajit.devicemanagement.ui.auth.LoginFragment
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {

    private val userId = LoginFragment.UserSession.loggedInUser?.id

    private val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
        private val collectionRef : CollectionReference = firestore.collection("user/${userId}/devices")



    fun addDevice(device: RoomDevice, callback: (Boolean) -> Unit) {
        Log.d("FirestoreRepository", "Adding device to Firestore: $userId")

        val deviceQuery = firestore.collectionGroup("devices")
            .whereEqualTo("deviceId", device.deviceId)

        deviceQuery.get().addOnSuccessListener { querySnapshot ->
            if (querySnapshot.isEmpty) {
                collectionRef.document(device.deviceId)
                    .set(device)
                    .addOnSuccessListener {
                        Log.d("FirestoreRepository", "Added device")
                        callback(true)
                    }
                    .addOnFailureListener { exception ->
                        Log.e("FirestoreRepository", "Failed to add device: ${exception.message}")
                        callback(false)
                    }
            } else {
                Log.d("FirestoreRepository", "Device already exists in Firestore")
                callback(true)
            }
        }
            .addOnFailureListener { exception ->
                Log.e("FirestoreRepository", "Query failed: ${exception.message}")
                callback(false)
            }
    }

    fun updateDevice(device: RoomDevice, callback: (Boolean) -> Unit) {
            val documentRef = collectionRef.document(device.deviceId)
            documentRef
                .set(device)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        }

        fun deleteDevice(deviceId: String, callback: (Boolean) -> Unit) {
            Log.e("FirestoreRepository"," delete  device firebase called $deviceId")

            val documentRef = collectionRef.document(deviceId)
            documentRef
                .delete()
                .addOnSuccessListener {
                    callback(true)
                    Log.e("FirestoreRepository","   device deleted from firebase  :$deviceId")

                }
                .addOnFailureListener {
                    callback(false)
                }
        }




    fun fetchDevicesFromFirestore(callback: (List<RoomDevice>) -> Unit) {
        val devices = mutableListOf<RoomDevice>()

        collectionRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e("fetchDevicesFromFirestore","fething devices from $collectionRef")
                for (document in task.result) {
                    val deviceId = document.getString("deviceId") ?: ""
                    val employeeId = document.getString("employeeId") ?: ""
                    val deviceType = document.getString("deviceType") ?: ""
                    val deviceName = document.getString("deviceName") ?: ""
                    val model = document.getString("model") ?: ""
                    val manufacturer = document.getString("manufacturer") ?: ""
                    val identificationId = document.getString("identificationId") ?: ""
                    val otherDetails = document.getString("otherDetails") ?: ""
                    val date = document.getLong("date") ?: 0L
                    val lastModified =
                        document.getLong("lastModified") ?: System.currentTimeMillis()

                    val device = RoomDevice(
                        deviceId = deviceId,
                        employeeId = employeeId,
                        deviceType = deviceType,
                        deviceName = deviceName,
                        model = model,
                        manufacturer = manufacturer,
                        identificationId = identificationId,
                        otherDetails = otherDetails,
                        date = date,
                        lastModified = lastModified,
                    )
                    devices.add(device)
                }
                callback(devices)
            } else {
                callback(emptyList()) // Return an empty list on failure
            }
        }
    }




}
