//package com.ajit.devicemanagement.data.repository
//
//import com.ajit.devicemanagement.ui.auth.AuthViewModel
//import com.ajit.devicemanagement.util.UiState
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.mockito.Mock
//import org.mockito.Mockito.*
//import org.mockito.MockitoAnnotations
//
//@ExperimentalCoroutinesApi
//class AuthViewModelTest {
//
//    private val testDispatcher = TestCoroutineDispatcher()
//
//    @Mock
//    private lateinit var repository: AuthRepository
//
//    private lateinit var viewModel: AuthViewModel
//
//    @Before
//    fun setup() {
//        MockitoAnnotations.initMocks(this)
//        Dispatchers.setMain(testDispatcher)
//        viewModel = AuthViewModel(repository)
//    }
//
//    @After
//    fun cleanup() {
//        Dispatchers.resetMain()
//        testDispatcher.cleanupTestCoroutines()
//    }
//
//    @Test
//    fun testLogin() = runBlockingTest {
//        // Mock the necessary dependencies and data
//        `when`(repository.loginUser(anyString(), anyString())).thenReturn(UiState.Success("Login successful"))
//
//        // Call the login method
//        viewModel.login("email", "password")
//
//        // Verify that the repository's loginUser method was called with the correct arguments
//        verify(repository).loginUser("email", "password")
//
//        // Verify that the login LiveData received the expected result
//        verify(viewModel.login).value = UiState.Success("Login successful")
//    }
//}
