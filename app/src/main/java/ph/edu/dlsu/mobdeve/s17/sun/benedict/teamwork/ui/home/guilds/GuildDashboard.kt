package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentGuildDashboardBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.posts.GuildPostsActivity
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.profile.GuildProfileActivity

/**
 * A simple [Fragment] subclass.
 */
class GuildDashboard : Fragment() {
    private lateinit var binding : FragmentGuildDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGuildDashboardBinding.inflate(inflater, container, false)
        val view = binding.root

        setEventHandlers(view)
        return view
    }

    private fun setEventHandlers(view : View){
        binding.mcvGuildProfile.setOnClickListener {
            startActivity(Intent(activity, GuildProfileActivity::class.java))
        }

        binding.mcvGuildPosts.setOnClickListener {
            startActivity(Intent(activity, GuildPostsActivity::class.java))
        }

        binding.mcvGuildChallenges.setOnClickListener {

        }
    }
}