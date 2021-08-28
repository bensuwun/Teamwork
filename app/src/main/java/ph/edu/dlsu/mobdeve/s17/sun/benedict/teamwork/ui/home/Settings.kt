package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentSettingsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.authentication.MainActivity

/**
 * A simple [Fragment] subclass.
 */
class Settings : Fragment() {

    lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater)

        binding.tempLogoutBtn.setOnClickListener {
            Toast.makeText(this.activity?.applicationContext, "Logged Out!", Toast.LENGTH_SHORT).show()
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut()
            // Sign out from Google
            GoogleSignIn.getClient((this.activity as HomeActivity).applicationContext,GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut()
            val mainActivityIntent = Intent(this.activity?.applicationContext, MainActivity::class.java)
            startActivity(mainActivityIntent)
            this.activity?.finish()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    fun tempLogout() {

    }

}