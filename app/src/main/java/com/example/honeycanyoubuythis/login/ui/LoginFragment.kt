package com.example.honeycanyoubuythis.login.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.honeycanyoubuythis.R
import com.example.honeycanyoubuythis.databinding.LoginFragmentBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LoginFragment : Fragment() {
    private var _binding: LoginFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        navController = findNavController()

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

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            task.exception.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }


    }

    private fun updateUI(user: FirebaseUser?) {
        println("got here $user")
        if (user != null) {
            val navController = findNavController()
            navController.navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }
}