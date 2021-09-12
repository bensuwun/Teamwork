package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildMemberDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.PostDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentGuildDashboardBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.posts.GuildPostsActivity
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class GuildDashboard : Fragment() {
    private lateinit var binding : FragmentGuildDashboardBinding
    private val TAG : String = "GuildDashboard"

    // Guild Info
    private lateinit var guild : Guild

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Broadcast Received")
            when(intent?.action) {
                intentLeftGuild -> {
                    Toast.makeText(context, "You left ${guild.name}", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.navigateBackToMyGuilds)
                }
            }

        }
    }
    private val intentLeftGuild : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.left_guild"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        // Initialize this guild's info
        initGuildInfo()

        // Inflate the layout for this fragment
        binding = FragmentGuildDashboardBinding.inflate(inflater, container, false)
        val view = binding.root

        // Update toolbar
        (activity as AppCompatActivity).supportActionBar?.title = guild?.name

        // Register broadcast receiver
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter(intentLeftGuild))

        setEventHandlers(view)
        return view
    }

    /**
     * Initializes this guild's information, retrieved from bundle.
     */
    private fun initGuildInfo() {
        try{
            guild = arguments?.getParcelable("guild")!!
        } catch(e: Exception) {
            e.message?.let { Log.e(TAG, it) }
        }
    }

    /**
     * Set event handlers for when:
     *  (1) User clicks Guild Profile
     *  (2) User clicks Guild Posts
     */
    private fun setEventHandlers(view : View){
        val bundle = Bundle()
        bundle.putParcelable("guild", guild)
        binding.mcvGuildProfile.setOnClickListener {
            // startActivity(Intent(activity, GuildProfileActivity::class.java))
            view.findNavController().navigate(R.id.navigateToGuildProfileFragment, bundle)
        }

        binding.mcvGuildPosts.setOnClickListener {
            // startActivity(Intent(activity, GuildPostsActivity::class.java))
            view.findNavController().navigate(R.id.navigateToGuildPostsActivity, bundle)
        }
    }

    /**
     * Add options: Leave guild
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.guild_dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    /**
     * Handle onclick event for options menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.option_leave_guild -> {
                GuildDAO(requireContext()).decrementGuildMemberCount(guild.name)
                UserPreferences.getUserAuthUid()?.let {
                    GuildMemberDAO(requireContext()).leaveGuild(guild.name,
                        it
                    )
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        super.onStop()
    }
}