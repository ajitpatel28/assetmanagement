package com.ajit.devicemanagement.ui.auth

import com.ajit.devicemanagement.data.model.User

interface AuthListener {
    fun registerSuccess(email: String?, password: String?, user: User)
    fun registerFailure(exception: Exception?, email: String?, password: String?,user: User)

    fun logInSuccess(email: String?, password: String?)
    fun logInFailure(exception: Exception?, email: String?, password: String?)
}