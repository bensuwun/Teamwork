package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.AllGuildsAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentSearchGuildsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild

/**
 *  Displays the list of all currently existing guilds in a RecyclerView.
 */
class SearchGuilds : Fragment() {
    private lateinit var binding : FragmentSearchGuildsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchGuildsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Dummy data for guilds
        var guilds : ArrayList<Guild> = Guild.initSampleData()


        // Inflate recycler view
        binding.rvSearchGuilds.layoutManager = LinearLayoutManager(activity)
        binding.rvSearchGuilds.adapter = context?.let { AllGuildsAdapter(guilds, it) }

        return view
    }
}