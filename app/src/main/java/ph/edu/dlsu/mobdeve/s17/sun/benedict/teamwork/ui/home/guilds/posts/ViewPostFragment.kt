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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.GuildCommentsAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.PostDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentViewPostBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Comment
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Post
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.TimestampParser
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class ViewPostFragment : Fragment() {
    private lateinit var binding : FragmentViewPostBinding
    private val TAG = "ViewPostFragment"

    private lateinit var guild : Guild
    private lateinit var post : Post
    private lateinit var postDAO : PostDAO
    private var newComment = Comment()

    private lateinit var comments : ArrayList<Comment>
    private lateinit var adapter : GuildCommentsAdapter

    // Broadcast variables
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Broadcast received")
            when(intent?.action){
                intentCommentsLoaded -> {
                    var bundle = intent?.extras
                    comments = bundle!!.getParcelableArrayList("comments")!!
                    for (comment in comments) {
                        Log.d(TAG, comment.comment)
                    }
                    adapter.notifyDataSetChanged()

                }
                intentCommentAdded -> {
                    Toast.makeText(context, "Comment added", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Obtain guild and post from intent
        guild = arguments?.getParcelable("guild")!!
        post = arguments?.getParcelable("post")!!

        // Inflate the layout for this fragment
        binding = FragmentViewPostBinding.inflate(inflater, container, false)
        val view = binding.root

        // Update post values
        updateViews()

        // Initialize recycler view for comments
        postDAO = PostDAO(requireContext())
        comments = ArrayList()
        adapter = GuildCommentsAdapter(comments, requireContext(), guild)
        binding.rvComments.layoutManager = LinearLayoutManager(context)
        binding.rvComments.adapter = adapter

        // Register broadcast receiver
        val intentFilters = IntentFilter()
        intentFilters.addAction(intentCommentAdded)
        intentFilters.addAction(intentCommentsLoaded)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, intentFilters)

        // Retrieve comments
        postDAO.getComments(guild.name, post.docId)

        // Add comment event listener
        binding.ivAddComment.setOnClickListener {
            val text = binding.etComment.text.toString().trim()
            if (text != "") {
                // TODO: Add comment to firestore
                newComment.author = UserPreferences(requireContext()).getLoggedInUser()!!
                newComment.comment = text
                newComment.date_commented = Timestamp(Date())
                postDAO.addComment(guild.name, post.docId, newComment)
            }
        }

        return view
    }

    private fun updateViews() {
        binding.post.tvUsername.text = post.author.username
        Glide.with(requireContext())
            .load(post.author.profileImage)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.placeholder_guild_dp)
            .into(binding.post.sivUserDp)
        binding.post.tvDatePosted.text = "Posted on: ${TimestampParser(post.date_posted).getDate()}"
        binding.post.tvPostTitle.text = post.title
        binding.post.tvPostDescription.text = post.description
        binding.post.tvComments.text = post.comments.toString()
        // Chips
        if(post.tags.challenge || post.tags.support || post.tags.social){
            if(!post.tags.challenge) binding.post.chipChallenge.visibility = View.GONE
            if(!post.tags.support) binding.post.chipSupport.visibility = View.GONE
            if(!post.tags.social) binding.post.chipSocial.visibility = View.GONE
        }
        else{
            binding.post.cgTags.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
    }

    companion object {
        val intentCommentsLoaded = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_comments_loaded"
        val intentCommentAdded = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_comment_added"
    }
}