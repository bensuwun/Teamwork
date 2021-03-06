package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentGuildsBinding

/**
 * Main fragment displayed in HomeActivity. Displays the My Guilds and Search Guilds buttons.
 */
class Guilds : Fragment() {
    private lateinit var binding : FragmentGuildsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGuildsBinding.inflate(inflater, container, false)

        val view = binding.root

        // My Guilds | Search Guilds
        setEventHandlers(view)
        return view
    }

    private fun setEventHandlers(view: View){
        binding.mcvMyGuilds.setOnClickListener {
            // If user does not have guilds
            // view.findNavController().navigate(R.id.navigateToMyGuildsEmpty)

            // Else
            view.findNavController().navigate(R.id.navigateToMyGuilds)
        }
        binding.mcvSearchGuilds.setOnClickListener {
            view.findNavController().navigate(R.id.navigateToSearchGuilds)
        }

    }

}