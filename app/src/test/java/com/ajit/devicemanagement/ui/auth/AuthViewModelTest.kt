package com.ajit.devicemanagement.ui.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import com.ajit.devicemanagement.data.model.User
import com.ajit.devicemanagement.data.repository.AuthRepository
import com.ajit.devicemanagement.data.repository.AuthRepositoryImpl
import com.ajit.devicemanagement.util.UiState
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(manifest= Config.NONE)
class AuthViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authRepository: AuthRepository


    @Mock
    private lateinit var gson: Gson

    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        authViewModel = AuthViewModel(authRepository)

    }

    @Test
    fun registerUser_success() = runBlockingTest {
        // Arrange
        val email = "test@example.com"
        val password = "password"
        val user = User(name = "John Doe", employeeId = "1234567890", email = "test@example.com")
        val successState = UiState.Success("User registered successfully!")
        val loadingState = UiState.Loading

        var callback: ((UiState<String>) -> Unit)? = null

        `when`(authRepository.registerUser(email, password, user, result = { state ->
            callback = null
        })).then {
            Thread.sleep(1000)
            callback?.invoke(successState)
            null
        }

        // Act
        authViewModel.register(email, password, user)

        // Assert
        assertEquals(loadingState, authViewModel.register.value)
        testScheduler.apply { advanceTimeBy(1000); runCurrent() } // Advance the time by 1000 milliseconds to simulate the delay
        assertEquals(successState, authViewModel.register.value)
    }



}