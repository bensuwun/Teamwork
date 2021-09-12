package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildMemberDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentGuildPreviewBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences

/**
 * A simple [Fragment] subclass.
 */
class GuildPreviewFragment : Fragment() {
    private lateinit var binding : FragmentGuildPreviewBinding
    private val TAG = "GuildPreviewFragment"

    // Guild Information
    private lateinit var guild : Guild

    // Broadcast variables
    private val intentMemberCheck = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_member"
    private val intentJoinedGuild = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_join_success"
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Broadcast Received")
            if (intent != null) {
                when(intent.action){
                    intentMemberCheck -> {
                        val bundle = intent.extras
                        val isAMember = bundle?.getBoolean("isAMember")
                        if (isAMember == true){
                            binding.mbtnAction.isEnabled = false
                        }
                    }
                    intentJoinedGuild -> {
                        val bundle = Bundle()
                        bundle.putParcelable("guild", guild)
                        Toast.makeText(context, "Successfully joined ${guild.name}", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.fromGuildPreviewNavigateToDashboard, bundle)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGuildPreviewBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize guild information from bundle
        initGuildInfo()

        // Register broadcast receiver
        val filter = IntentFilter()
        filter.addAction(intentMemberCheck)
        filter.addAction(intentJoinedGuild)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, filter)

        // Update views
        binding.tvGuildName.text = guild.name
        binding.tvGuildMembers.text = getString(R.string.member_count, guild.memberCount)
        binding.tvGuildDescription.text = guild.description
        Glide.with(requireContext())
            .load(guild.profileImage)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(binding.sivGuildDp)
        Glide.with(requireContext())
            .load(guild.headerImage)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .into(binding.ivGuildHeader)

        // Set event handlers
        setEventHandlers()
        return view
    }

    /**
     * Initializes guild information from intent bundle
     */
    private fun initGuildInfo() {
        // Set basic guild information
        try{
            /*
            guildId = arguments?.getString("guildId").toString()
            name = arguments?.getString("name").toString()
            memberCount = arguments?.getLong("member_count", 0)!!
            description = arguments?.getString("description").toString()
            */
            guild = arguments?.getParcelable("guild")!!
            UserPreferences.getUserAuthUid()?.let {
                GuildMemberDAO(requireContext()).isAMemberOf(guild.name,
                    it
                )
            }
        } catch(e : Error) {
            e.message?.let { Log.e(TAG, it) }
            Log.e(TAG, "Something went wrong when setting guild profile information. " +
                    "Make sure you passed the guildId, name, member_count, and description when navigating to this Activity")
        }
    }

    /**
     * Set event handlers: Join Guild
     */
    private fun setEventHandlers() {
        binding.mbtnAction.setOnClickListener {
            // Set document in guild_members collection
            UserPreferences.getUserAuthUid()?.let { it1 ->
                GuildMemberDAO(requireContext()).joinGuild(guild.name,
                    it1
                )
            }
            // Increment guild member count
            GuildDAO(requireContext()).incrementGuildMemberCount(guild.name)
        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }
}