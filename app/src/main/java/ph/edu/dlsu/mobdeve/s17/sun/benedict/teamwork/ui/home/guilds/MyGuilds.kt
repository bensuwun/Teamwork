package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.MyGuildsAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentMyGuildsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild

/**
 * A simple [Fragment] subclass.
 */
class MyGuilds : Fragment() {
    private lateinit var binding : FragmentMyGuildsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyGuildsBinding.inflate(inflater, container, false);

        val view = binding.root

        binding.rvMyGuilds.layoutManager = LinearLayoutManager(activity)
        binding.rvMyGuilds.adapter = context?.let { MyGuildsAdapter(Guild.initSampleData(), it) }

        return view
    }

}