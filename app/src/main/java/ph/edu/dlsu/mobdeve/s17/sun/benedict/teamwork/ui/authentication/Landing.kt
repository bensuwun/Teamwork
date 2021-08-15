package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentLandingBinding

/**
 * A simple [Fragment] subclass.
 */
class Landing : Fragment() {

    lateinit var binding: FragmentLandingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLandingBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize event handlers
        setEventHandlers(view)

        // Return the entire fragment
        return view
    }

    private fun setEventHandlers(view : View){
        binding.btnLogin.setOnClickListener {
            view.findNavController().navigate(R.id.navigateToLogin)
        }

        binding.btnRegister.setOnClickListener {
            view.findNavController().navigate(R.id.navigateToRegister)
        }
    }
}