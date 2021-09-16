package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.api.services.tasks.TasksScopes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.UserDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentLoginBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.User
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences


/**
 * A simple [Fragment] subclass.
 */
class Login : Fragment() {

    private val TAG: String = "LOGIN_FRAGMENT"
    lateinit var binding : FragmentLoginBinding
    lateinit var parentActivity: MainActivity
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var googleSignInClient: GoogleSignInClient
    private val fbAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        // Get the parent activity
        this.parentActivity = this.activity as MainActivity

        binding.btnLogin.setOnClickListener {
            // Null checking
            if(binding.editTextEmail.text.toString().isEmpty() || binding.editTextPassword.text.toString().isEmpty()) {
                Toast.makeText(this.parentActivity.applicationContext, "One or more fields are empty.", Toast.LENGTH_SHORT).show()
            } else {
                // Login via Firebase
                Toast.makeText(it.rootView.context, "Logging in...", Toast.LENGTH_SHORT).show()
                try { this.fbAuth.signInWithEmailAndPassword(binding.editTextEmail.text.toString(), binding.editTextPassword.text.toString()).addOnCompleteListener { authResult ->
                    if ( authResult.isSuccessful && authResult.result.user != null) {
                        // SAVE USER TO SHARED PREFERENCES
                        var userDAO = UserDAO()
                        val userPreferences = UserPreferences(requireContext())
                        userDAO.getUserByAuthId(authResult.result.user!!.uid) { success ->
                            if (success) {
                                userPreferences.saveLoggedInUser(userDAO.document as User)
                                view.findNavController().navigate(R.id.navigateToHome)
                            }
                        }
                        requireActivity().finish()
                    } else {
                        Toast.makeText(
                            it.rootView.context,
                            "Login failed: " + (authResult.exception?.message ?: "Unknown Error"),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }} catch (ex: Exception) { Toast.makeText(requireContext(), ex.localizedMessage, Toast.LENGTH_SHORT) }
            }
        }
        
        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            //.requestScopes(Scope(TasksScopes.TASKS))
            //.requestServerAuthCode(getString(R.string.default_web_client_id))
            .build()

        googleSignInClient = GoogleSignIn.getClient(this.parentActivity, gso)

        this.resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("googleLogin", "Result reached")
            Log.d("googleLogin", "${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                Log.d("login", "Returned Result Code RC_SIGN_IN")
                val data: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                    val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                    this.parentActivity.fbAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // CHECK IF THE USER HAS A FIRESTORE ENTRY
                            var userDAO = UserDAO()
                            val userPreferences = UserPreferences(requireContext())
                            userDAO.getUserByAuthId(task.result.user!!.uid) {
                                if(userDAO.document != null) {
                                    // User account exists - store to UserPreferences
                                    userPreferences.saveLoggedInUser(userDAO.document as User)
                                    view.findNavController().navigate(R.id.navigateToHome)
                                    activity?.finish()
                                } else {
                                    // No user account, create a new one
                                    val newUser = User()
                                    newUser.authUid = task.result.user!!.uid
                                    newUser.username = task.result.user!!.email!!.split("@")[0]
                                    userDAO.document = newUser
                                    userDAO.createBlankUser {
                                        if(it) {
                                            // Store to UserPreferences
                                            userPreferences.saveLoggedInUser(newUser)
                                            view.findNavController().navigate(R.id.navigateToHome)
                                            activity?.finish()
                                        } else {
                                            // Reject
                                            Toast.makeText(requireContext(), "Unable to create new Firestore user.", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                this.parentActivity.applicationContext,
                                "Login Failed: ${(task.exception?.localizedMessage) ?: "Unknown Error"}.",
                                Toast.LENGTH_LONG
                            ).show()
                            GoogleSignIn.getClient(this.parentActivity.applicationContext,GoogleSignInOptions.Builder(
                                GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut()
                        }
                    }
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG, "Google sign in failed", e)
                }
            }
        }

        // Add the OnClickListener for the Google Sign In Button
        binding.btnGoogleLogin.setOnClickListener { view ->
            val signInIntent = this.googleSignInClient.signInIntent
            this.resultLauncher.launch(signInIntent)
        }

        return view
    }
}