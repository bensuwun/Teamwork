package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
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
    private val TAG : String = "GuildDashboard"
    private lateinit var guildName : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGuildDashboardBinding.inflate(inflater, container, false)
        val view = binding.root

        // Update toolbar
        guildName = arguments?.getString("guild_name").toString()
        (activity as AppCompatActivity).supportActionBar?.title = guildName

        setEventHandlers(view)
        return view
    }

    private fun setEventHandlers(view : View){
        val bundle : Bundle = bundleOf("guild_name" to guildName)
        binding.mcvGuildProfile.setOnClickListener {
            // startActivity(Intent(activity, GuildProfileActivity::class.java))
            view.findNavController().navigate(R.id.navigateToGuildProfileActivity, bundle)
        }

        binding.mcvGuildPosts.setOnClickListener {
            // startActivity(Intent(activity, GuildPostsActivity::class.java))
            view.findNavController().navigate(R.id.navigateToGuildPostsActivity, bundle)
        }

        binding.mcvGuildChallenges.setOnClickListener {

        }
    }
}