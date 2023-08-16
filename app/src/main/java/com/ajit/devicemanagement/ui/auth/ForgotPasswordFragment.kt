package com.ajit.devicemanagement.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ajit.devicemanagement.R
import com.ajit.devicemanagement.databinding.FragmentForgotPasswordBinding
import com.ajit.devicemanagement.ui.base.BaseFragment
import com.ajit.devicemanagement.util.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ForgotPasswordFragment : BaseFragment() {

    val TAG: String = "ForgotPasswordFragment"
    lateinit var binding: FragmentForgotPasswordBinding
    val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()


        binding.forgetBack.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
        }
        binding.forgetPassBtn.setOnClickListener {
            if (validation()) {
                Log.e(TAG,"valid ${validation()}")
                viewModel.forgotPassword(binding.emailEt.text.toString())
            }
        }


    }

    private fun observer() {
        viewModel.forgotPassword.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.forgetPassBtn.text = ""
                    binding.forgetPassProgress.show()
                }
                is UiState.Failure -> {
                    binding.forgetPassBtn.text = "Send"
                    binding.forgetPassProgress.hide()
                    toast(state.error)
                }
                is UiState.Success -> {
                    binding.forgetPassBtn.text = "Send"
                    binding.forgetPassProgress.hide()
                    toast(state.data)
                }
            }
        }
    }

    fun validation(): Boolean {
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

        return isValid
    }


}