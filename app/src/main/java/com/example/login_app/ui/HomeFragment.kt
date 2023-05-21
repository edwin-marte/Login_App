package com.example.login_app.ui

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.login_app.R
import com.example.login_app.databinding.FragmentHomeBinding
import com.example.login_app.presentation.MainViewModel
import com.example.login_app.core.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userEmail = requireArguments().getString("email") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserSharedPrefs(userEmail)
        setupSignOutButton()
    }

    private fun setupUserSharedPrefs(userEmail: String) {
        val prefs = requireActivity().getSharedPreferences(resources.getString(R.string.prefs_file), MODE_PRIVATE).edit()
        prefs.putString("email", userEmail)
        prefs.apply()
        fillUI()
    }

    private fun setupSignOutButton() {
        binding.signOutButton.setOnClickListener {
            val prefs = requireActivity().getSharedPreferences(resources.getString(R.string.prefs_file), MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            viewModel.signOut.observe(viewLifecycleOwner, Observer { result ->
                when (result) {
                    is Resource.Success -> {
                        findNavController().navigateUp()
                    }
                    is Resource.Failure -> {
                        showMsj("Error occurred while signing out", result.exception)
                    }
                }
            })
        }
    }

    private fun fillUI() {
        binding.userEmail.text = userEmail
    }

    private fun showMsj(msj: String, e: Exception?) {
        Toast.makeText(requireContext(), "$msj ${e ?: ""}", Toast.LENGTH_SHORT).show()
    }
}