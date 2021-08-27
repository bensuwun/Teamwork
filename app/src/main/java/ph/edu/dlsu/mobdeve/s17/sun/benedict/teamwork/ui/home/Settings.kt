package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.tempLogoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val mainActivityIntent = Intent(this.activity?.applicationContext, MainActivity::class.java)
            startActivity(mainActivityIntent)
            this.activity?.finish()
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    fun tempLogout() {

    }

}