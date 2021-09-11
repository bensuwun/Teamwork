package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.appbar.AppBarLayout
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildMemberDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentGuildProfileBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences

/**
 * A simple [Fragment] subclass.
 */
class GuildProfileFragment : Fragment() {
    private lateinit var binding : FragmentGuildProfileBinding
    private val TAG = "GuildProfileFragment"

    // Guild Information
    private lateinit var guildId : String
    private lateinit var name : String
    private var memberCount : Long = 0
    private lateinit var description : String
    private lateinit var guildImg : String
    private lateinit var guildHeader : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGuildProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize guild information from bundle
        initGuildInfo()

        // Update views
        binding.tvGuildName.text = name
        binding.tvGuildMembers.text = getString(R.string.member_count, memberCount)
        binding.tvGuildDescription.text = description

        return view
    }

    /**
     * Initializes guild information from intent bundle
     */
    private fun initGuildInfo() {
        // Set basic guild information
        try{
            guildId = arguments?.getString("guildId").toString()
            name = arguments?.getString("name").toString()
            memberCount = arguments?.getLong("memberCount", 0)!!
            description = arguments?.getString("description").toString()
            UserPreferences.getUserAuthUid()?.let {
                GuildMemberDAO(requireContext()).isAMemberOf(guildId,
                    it
                )
            }
        } catch(e : Error) {
            e.message?.let { Log.e(TAG, it) }
            Log.e(TAG, "Something went wrong when setting guild profile information. " +
                    "Make sure you passed the guildId, name, member_count, and description when navigating to this Activity")
        }
    }
}