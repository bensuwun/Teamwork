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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.GuildPostAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.PostDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentGuildPostsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Post


/**
 * A simple [Fragment] subclass.
 */
class GuildPostsFragment : Fragment() {
    private lateinit var binding : FragmentGuildPostsBinding
    private val TAG = "GuildPostsFragment"

    // Guild information
    private lateinit var guild : Guild

    // RecyclerView variables
    private lateinit var adapter : GuildPostAdapter
    private lateinit var posts : ArrayList<Post>

    // Broadcast variables
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Broadcast received")
            when(intent?.action){
                intentPostsLoaded -> {
                    // Retrieve posts
                    var bundle = intent?.extras
                    var posts : ArrayList<Post> = bundle!!.getParcelableArrayList("posts")!!
                    adapter.setData(posts)
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get data from activity (https://stackoverflow.com/questions/50334550/navigation-architecture-component-passing-argument-data-to-the-startdestination)
        guild = requireActivity().intent?.extras?.getParcelable<Guild>("guild")!!
        (activity as AppCompatActivity).supportActionBar?.title = guild.name

        // Inflate the layout for this fragment
        binding = FragmentGuildPostsBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize recyclerView variables
        posts = ArrayList()
        adapter = GuildPostAdapter(posts, requireContext(), guild)

        // Populate recyclerView
        binding.rvGuildPosts.layoutManager = LinearLayoutManager(activity)
        binding.rvGuildPosts.adapter = adapter

        // Obtain posts for this guild (Get intent from parent Activity)
        Log.d(TAG, guild.name)
        PostDAO(requireContext()).getGuildPosts(guild.name)

        setEventListeners()

        return view
    }


    private fun setEventListeners(){
        val bundle = Bundle()
        bundle.putParcelable("guild", guild)
        binding.fabAddPost.setOnClickListener {
            it.findNavController().navigate(R.id.navigateToAddGuildPostFragment, bundle)
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, IntentFilter(intentPostsLoaded))
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        super.onStop()
    }

    companion object {
       val intentPostsLoaded : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_posts_loaded"
    }
}