package com.ajit.devicemanagement.ui.auth

import com.ajit.devicemanagement.data.provider.DataSyncManager
import android.content.Context
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ajit.devicemanagement.R
import com.ajit.devicemanagement.data.model.User
import com.ajit.devicemanagement.databinding.FragmentLoginBinding
import com.ajit.devicemanagement.ui.base.BaseFragment
import com.ajit.devicemanagement.util.*
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    object UserSession {
        var loggedInUser: User? = null
    }

    private lateinit var binding: FragmentLoginBinding
    private lateinit var dataSyncManager: DataSyncManager
    private val viewModel: AuthViewModel by viewModels()
    private var isPasswordVisible = false



    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataSyncManager = DataSyncManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()


        binding.toggleBtn.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.passEt.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.toggleBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_open, 0)
            } else {
                binding.passEt.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.toggleBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0)
            }
            binding.passEt.setSelection(binding.passEt.text.length) // Maintain cursor position
        }


        binding.loginBtn.setOnClickListener {
            if (validation()) {
                viewModel.login(
                    email = binding.emailEt.text.toString(),
                    password = binding.passEt.text.toString()
                )
            }
        }

        binding.forgotpassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        binding.registerLabel.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun observe() {
        viewModel.login.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.loginBtn.setText("")
                    binding.loginProgress.show()
                }
                is UiState.Failure -> {
                    binding.loginBtn.setText("Login")
                    binding.loginProgress.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.loginBtn.setText("Login")
                    binding.loginProgress.hide()
                    toast(state.data)
                    viewModel.getSession { user ->
                        if (user != null) {
                            UserSession.loggedInUser = user
                            dataSyncManager.startSync{
                            findNavController().navigate(R.id.action_loginFragment_to_dashboardFragment)
                        }
                    }
                    }
                }
            }
        }
    }

    private fun validation(): Boolean {
        var isValid = true

        if (binding.emailEt.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_email))
        } else {
            if (!binding.emailEt.text.toString().isValidEmail()) {
                isValid = false
                toast(getString(R.string.invalid_email))
            }
        }
        if (binding.passEt.text.isNullOrEmpty()) {
            isValid = false
            toast(getString(R.string.enter_password))
        } else {
            if (binding.passEt.text.toString().length < 8) {
                isValid = false
                toast(getString(R.string.invalid_password))
            }
        }
        return isValid
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSession { user ->
            if (user != null) {
                UserSession.loggedInUser = user
                dataSyncManager.startSync {
                    findNavController().navigate(
                        R.id.action_loginFragment_to_dashboardFragment,
                        Bundle().apply {
                            putParcelable("user", user)
                        })
                }

            }
        }
    }


}
