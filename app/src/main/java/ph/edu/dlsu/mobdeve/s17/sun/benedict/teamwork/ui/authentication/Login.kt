package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.authentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentLoginBinding


/**
 * A simple [Fragment] subclass.
 */
class Login : Fragment() {

    private val TAG: String = "LOGIN_FRAGMENT"
    lateinit var binding : FragmentLoginBinding
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        this.firebaseAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        this.googleSignInClient =
            this.activity?.let { GoogleSignIn.getClient(it.applicationContext, gso) }!!

        binding.btnLogin.setOnClickListener {
            // Login via Firebase
            Toast.makeText(it.rootView.context, "Logging in...", Toast.LENGTH_SHORT).show()
            this.firebaseAuth.signInWithEmailAndPassword(binding.editTextEmail.text.toString(), binding.editTextPassword.text.toString()).addOnCompleteListener { authResult ->
                Log.d(TAG, authResult.exception.toString())
                Log.d(TAG, binding.editTextEmail.text.toString())
                Log.d(TAG, binding.editTextPassword.text.toString())
                if (authResult.isSuccessful) {
                    view.findNavController().navigate(R.id.navigateToHome)
                    activity?.finish()
                } else {
                    Toast.makeText(
                        it.rootView.context,
                        "Login failed: " + (authResult.exception?.message ?: "Unknown Error"),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        return view
    }

}