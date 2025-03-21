package com.example.honeycanyoubuythis.login.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.honeycanyoubuythis.R
import com.example.honeycanyoubuythis.database.AppDatabase
import com.example.honeycanyoubuythis.databinding.LoginFragmentBinding
import com.example.honeycanyoubuythis.login.viewmodel.LoginViewModel
import com.example.honeycanyoubuythis.login.viewmodel.LoginViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        val currentUserDao = AppDatabase.getInstance(requireContext()).currentUserDao()
        val factory = LoginViewModelFactory(currentUserDao)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        navController = findNavController()

        lifecycleScope.launch {
            if (loginViewModel.checkIfLoggedIn()) {
                updateUI()
            }
        }
        with(binding) {
            emailField.addTextChangedListener(loginTextWatcher)
            passwordField.addTextChangedListener(loginTextWatcher)
            notRegisteredText.setOnClickListener {
                navController.navigate(R.id.action_loginFragment_to_registerFragment)
            }
            loginButton.setOnClickListener {
                performLogin()
            }
        }

    }

    private val loginTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            with(binding) {
                val email = emailField.text.toString().trim()
                val password = passwordField.text.toString().trim()

                loginButton.isEnabled = email.isNotEmpty() && password.isNotEmpty()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun performLogin() {
        with(binding) {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            lifecycleScope.launch {
                if (loginViewModel.performLogin(email, password)) {
                    updateUI()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Login Failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateUI() {
        navController = findNavController()
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true)
            .build()
        navController.navigate(R.id.action_loginFragment_to_homeFragment, null, navOptions)
    }
}