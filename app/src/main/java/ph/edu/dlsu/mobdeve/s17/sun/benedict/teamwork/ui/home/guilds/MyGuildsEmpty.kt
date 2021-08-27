package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentMyGuildsEmptyBinding

/**
 * Displayed when the currently logged in user does not have any guilds.
 */
class MyGuildsEmpty : Fragment() {
    private lateinit var binding : FragmentMyGuildsEmptyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyGuildsEmptyBinding.inflate(inflater, container, false)
        val view = binding.root

        setEventHandlers(view)

        return view
    }

    private fun setEventHandlers(view : View){
        binding.btnSearchGuilds.setOnClickListener {
            view.findNavController().navigate(R.id.fromEmptyGuildsNavigateToSearchGuilds)
        }
    }

}