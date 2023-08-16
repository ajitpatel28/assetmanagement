    package com.ajit.devicemanagement.data.repository

    import android.net.Uri
    import com.ajit.devicemanagement.data.model.User
    import com.ajit.devicemanagement.util.UiState



    interface AuthRepository {
        fun registerUser(email: String, password: String, user: User, result: (UiState<String>) -> Unit)
        fun updateUserInfo(user: User, result: (UiState<String>) -> Unit)
        fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit)
        fun forgotPassword(email: String, result: (UiState<String>) -> Unit)
        fun logout(result: () -> Unit)
        fun updateUser(user: User, result: (UiState<String>) -> Unit)
        fun storeSession(id: String, result: (User?) -> Unit)
        fun getSession(result: (User?) -> Unit)
        fun uploadProfilePicture(userId: String, imageUri: Uri, result: (UiState<String>) -> Unit)


        }
