package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.posts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.GuildPostAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentGuildPostsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Post


/**
 * A simple [Fragment] subclass.
 */
class GuildPostsFragment : Fragment() {
    private lateinit var binding : FragmentGuildPostsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentGuildPostsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.rvGuildPosts.layoutManager = LinearLayoutManager(activity)
        binding.rvGuildPosts.adapter = context?.let { GuildPostAdapter(Post.initSampleData(), it) }
        return view
    }
}