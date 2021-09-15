package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.UserDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentRegisterBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.User
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences

/**
 * A simple [Fragment] subclass.
 */
class Register : Fragment() {

    lateinit var binding: FragmentRegisterBinding
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this.binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val v = this.binding.root

        this.auth = FirebaseAuth.getInstance()

        initialize()

        // Inflate the layout for this fragment
        return v
    }

    private fun initialize() {
        // Set the onClickListener for the register button
        this.binding.btnRegister.setOnClickListener { v ->
            val emailAddress = this.binding.etEmailAddress.text.toString()
            val username = this.binding.etUsername.text.toString()
            val password = this.binding.etPwd.text.toString()
            val confirmPwd = this.binding.etConfirmPwd.text.toString()

            // Check if password matches
            if(emailAddress.isEmpty()) {
                Toast.makeText(this.requireContext(), "Enter an email address.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else if(password != confirmPwd) {
                Toast.makeText(this.requireContext(), "The passwords do not match.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else if(password.isEmpty()) {
                Toast.makeText(this.requireContext(), "Please enter a password.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            this.auth.createUserWithEmailAndPassword(emailAddress, password)
                .addOnSuccessListener { task ->
                    val newUser = User()
                    newUser.authUid = task.user!!.uid
                    newUser.profileImage = ""
                    newUser.username = username

                    val userDAO = UserDAO()
                    userDAO.document = newUser
                    userDAO.createBlankUser {
                        // Save to UserPreferences if good then login
                        UserPreferences(requireContext()).saveLoggedInUser(newUser)
                        findNavController().navigate(R.id.navigateToHome)
                        requireActivity().finish()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to create new user: " + e.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }
    }

}