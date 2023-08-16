package com.ajit.devicemanagement.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    var id: String = "",
    val employeeId: String = "",
    var name: String = "",
    val email: String = "",
    var mobileNumber: String = "",
    var profilePicture: String? = null,
    var isAdmin : Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable
