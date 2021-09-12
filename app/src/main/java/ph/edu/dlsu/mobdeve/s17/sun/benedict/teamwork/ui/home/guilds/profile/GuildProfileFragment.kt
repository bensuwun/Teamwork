package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildMemberDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentGuildProfileBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences

/**
 * A simple [Fragment] subclass.
 */
class GuildProfileFragment : Fragment() {
    private lateinit var binding : FragmentGuildProfileBinding
    private val TAG = "GuildProfileFragment"

    // Guild Information
    private lateinit var guild : Guild

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

        return view
    }

    /**
     * Initializes guild information from intent bundle
     */
    private fun initGuildInfo() {
        // Set basic guild information
        try{
            guild = arguments?.getParcelable("guild")!!
            UserPreferences.getUserAuthUid()?.let {
                GuildMemberDAO(requireContext()).isAMemberOf(guild.name,
                    it
                )
            }
        } catch(e : Error) {
            e.message?.let { Log.e(TAG, it) }
            Log.e(TAG, "Something went wrong when setting guild profile information. " +
                    "Make sure you passed the guild when you navigate to this fragment")
        }
    }
}