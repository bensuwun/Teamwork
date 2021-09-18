package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.posts

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.google.firebase.Timestamp
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.PostDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.UserDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentAddGuildPostBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.*
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AddGuildPostFragment : Fragment() {
    private lateinit var binding : FragmentAddGuildPostBinding
    private val TAG = "AddGuildPostFragment"

    // Guild Information
    private lateinit var guild : Guild
    private val newPost = Post()

    private lateinit var postDAO : PostDAO
    // User preference for logged in User object
    private lateinit var userPreferences : UserPreferences

    // Broadcast variables
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Broadcast received")
            when(intent?.action){
                intentPostAdded -> {
                    Toast.makeText(context, "Post added", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get guild from intent
        guild = arguments?.getParcelable<Guild>("guild")!!
        userPreferences = UserPreferences(requireContext())

        // Register broadcast receiver
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter(intentPostAdded))

        // Inflate the layout for this fragment
        binding = FragmentAddGuildPostBinding.inflate(inflater, container, false)
        val view = binding.root
        postDAO = PostDAO(requireContext())

        // Set add post handler
        setEventHandlers()

        return view
    }

    private fun setEventHandlers() {
        binding.btnAddPost.setOnClickListener {
            // Validation
            val title = binding.etPostTitle.text.toString()
            val description = binding.etPostDescription.text.toString()
            if (title.trim() != "") {
                if (description.trim() != "") {
                    // TODO: Get current timestamp
                    val timestamp = Timestamp(Date())

                    val tags = Tags()
                    tags.challenge = binding.chipChallenge.isChecked
                    tags.support = binding.chipSupport.isChecked
                    tags.social = binding.chipSocial.isChecked

                    newPost.title = title
                    newPost.author = userPreferences.getLoggedInUser()!!
                    newPost.authorUid = userPreferences.getLoggedInUser()!!.authUid
                    newPost.description = description
                    newPost.likes = 0
                    newPost.comments = 0
                    newPost.tags = tags
                    newPost.date_posted = timestamp

                    // Add post to firestore
                    postDAO.addGuildPost(guild.name, newPost)

                }
                else{
                    Toast.makeText(requireContext(), "Please provide a description", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(requireContext(), "Please provide a title", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }

    companion object {
        val intentPostAdded : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_posts_loaded"
    }
}