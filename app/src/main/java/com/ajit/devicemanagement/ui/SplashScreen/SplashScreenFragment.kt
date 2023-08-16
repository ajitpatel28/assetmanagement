package com.ajit.devicemanagement.ui.SplashScreen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ajit.devicemanagement.MainActivity
import com.ajit.devicemanagement.R
import com.ajit.devicemanagement.data.provider.DataSyncManager
import com.ajit.devicemanagement.databinding.FragmentSplashScreenBinding
import com.ajit.devicemanagement.ui.auth.AuthViewModel
import com.ajit.devicemanagement.ui.auth.LoginFragment
import com.ajit.devicemanagement.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenFragment : BaseFragment() {

    private lateinit var binding: FragmentSplashScreenBinding
    private val authViewModel : AuthViewModel by viewModels()


    @Inject
    lateinit var dataSyncManager: DataSyncManager



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashScreenBinding.inflate(layoutInflater)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.splashtext.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.downtoupanimation))
        binding.splashimg.startAnimation(AnimationUtils.loadAnimation(requireContext(),R.anim.alpha))

        Handler().postDelayed({
            authViewModel.getSession { user ->
                if (user != null) {
                    LoginFragment.UserSession.loggedInUser = user
                    dataSyncManager.startSync {
                        Log.e("SplashScreenFragment","data sync manager called")
                        findNavController().navigate(
                            R.id.action_splashScreenFragment_to_dashboardFragment,
                            Bundle().apply {
                                putParcelable("user", user)
                            })
                    }

                }
            else{
                    findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)

                }
            }
        }, 3000)
    }

}

