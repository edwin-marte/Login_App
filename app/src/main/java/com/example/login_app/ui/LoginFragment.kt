package com.example.login_app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.login_app.R
import com.example.login_app.databinding.FragmentLoginBinding
import com.example.login_app.presentation.MainViewModel
import com.example.login_app.core.Resource
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentLoginBinding
    private val GOOGLE_SIGN_IN_REQUEST_CODE = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabs()
        setupFirebaseLogin()
        session()

        binding.forgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_passwordResetFragment)
        }
    }

    private fun session() {
        val prefs = requireActivity().getSharedPreferences(
            resources.getString(R.string.prefs_file),
            Context.MODE_PRIVATE
        )
        
        val email = prefs.getString("email", null)

        if (email != null) {
            val bundle = Bundle()
            bundle.putString("email", email)
            clearUI()
            showHome(bundle)
        }
    }

    private fun validateEditTexts(vararg editTexts: EditText): Boolean {
        for (editText in editTexts) {
            if (editText.text.isNullOrBlank() || editText.text.isEmpty()) {
                showMsj("Error please fill all the fields", null)
                return false
            }
        }
        return true
    }

    private fun clearUI() {
        binding.emailEditText.setText("")
        binding.passwordEditText.setText("")
    }

    private fun setupFirebaseLogin() {
        binding.logInButton.setOnClickListener {
            if (!validateEditTexts(binding.emailEditText, binding.passwordEditText))
                return@setOnClickListener

            viewModel.login(
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString()
            ).observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Resource.Success -> {
                        val bundle = Bundle()
                        bundle.putString("email", binding.emailEditText.text.toString())
                        clearUI()
                        showHome(bundle)
                    }
                    is Resource.Failure -> {
                        showMsj(
                            "Error occurred while authenticating the user",
                            result.exception
                        )
                    }
                }
            })
        }

        binding.signUpButton.setOnClickListener {
            if (!validateEditTexts(binding.suEmailEditText, binding.suPasswordEditText))
                return@setOnClickListener

            if (binding.suPasswordEditText.text.toString() !=
                binding.suConfirmPasswordEditText.text.toString()
            ) {
                showMsj("Passwords doesn't match", null)
                return@setOnClickListener
            }

            if (binding.suPasswordEditText.text.toString().length < 6) {
                showMsj("The password must contain at least 6 characters", null)
                return@setOnClickListener
            }

            viewModel.signUp(
                binding.suEmailEditText.text.toString(),
                binding.suPasswordEditText.text.toString()
            ).observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Resource.Success -> {
                        val bundle = Bundle()
                        bundle.putString("email", binding.suEmailEditText.text.toString())
                        showHome(bundle)
                    }
                    is Resource.Failure -> {
                        showMsj(
                            "Error occurred while authenticating the user",
                            result.exception
                        )
                    }
                }
            })
        }

        binding.googleSignIn.setOnClickListener {
            val googleConfig = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

            val googleClient = GoogleSignIn.getClient(requireActivity(), googleConfig)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE)
        }
    }

    private fun setupTabs() {
        binding.logInTab.setOnClickListener {
            binding.logInTab.background =
                AppCompatResources.getDrawable(requireContext(), R.drawable.switch_element)
            binding.logInTab.setTextColor(resources.getColor((R.color.white), null))
            binding.logInLayout.visibility = View.VISIBLE

            binding.signUpTab.background = null
            binding.signUpTab.setTextColor(resources.getColor((R.color.gray_80), null))
            binding.signUpLayout.visibility = View.GONE

            binding.logInButton.visibility = View.VISIBLE
            binding.signUpButton.visibility = View.GONE
        }

        binding.signUpTab.setOnClickListener {
            binding.signUpTab.background =
                AppCompatResources.getDrawable(requireContext(), R.drawable.switch_element)
            binding.signUpTab.setTextColor(resources.getColor((R.color.white), null))
            binding.signUpLayout.visibility = View.VISIBLE

            binding.logInTab.background = null
            binding.logInTab.setTextColor(resources.getColor((R.color.gray_80), null))
            binding.logInLayout.visibility = View.GONE

            binding.signUpButton.visibility = View.VISIBLE
            binding.logInButton.visibility = View.GONE
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val bundle = Bundle()
                bundle.putString("email", account.email)
                showHome(bundle)
            } else {
                task.exception?.let {
                    showMsj("Error occurred while authenticating the user", it)
                }
                clearUI()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null)
                    firebaseAuthWithGoogle(account)

            } catch (e: ApiException) {
                showMsj("Error occurred while authenticating the user", e)
            }
        }
    }

    private fun showHome(bundle: Bundle) {
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment, bundle)
    }

    private fun showMsj(msj: String, e: Exception?) {
        Toast.makeText(requireContext(), "$msj ${e ?: ""}", Toast.LENGTH_LONG).show()
    }
}