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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.honeycanyoubuythis.R
import com.example.honeycanyoubuythis.database.AppDatabase
import com.example.honeycanyoubuythis.databinding.RegistrationFragmentBinding
import com.example.honeycanyoubuythis.login.viewmodel.RegisterViewModel
import com.example.honeycanyoubuythis.login.viewmodel.RegisterViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: RegistrationFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RegistrationFragmentBinding.inflate(inflater, container, false)
        val currentUserDao = AppDatabase.getInstance(requireContext()).currentUserDao()
        val factory = RegisterViewModelFactory(currentUserDao)
        registerViewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        with(binding) {
            emailField.addTextChangedListener(registerTextWatcher)
            passwordField.addTextChangedListener(registerTextWatcher)
            displayNameField.addTextChangedListener(registerTextWatcher)
            registerButton.setOnClickListener {
                performSignUp()
            }
        }
    }

    private val registerTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            with(binding) {
                val username = emailField.text.toString().trim()
                val password = passwordField.text.toString().trim()
                val displayName = displayNameField.text.toString().trim()

                registerButton.isEnabled =
                    username.isNotEmpty() && password.isNotEmpty() && displayName.isNotEmpty()
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun performSignUp() {
        val email = binding.emailField.text.toString().trim()
        val password = binding.passwordField.text.toString().trim()
        val displayName = binding.displayNameField.text.toString().trim()
        lifecycleScope.launch {
            val user = registerViewModel.performSignUp(email, password, displayName)
            updateUI(user)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val navController = findNavController()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build()

            navController.navigate(R.id.action_registerFragment_to_homeFragment, null, navOptions)
        } else {
            Toast.makeText(
                requireContext(),
                "Registration Failed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}