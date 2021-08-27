package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.GuildPostAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.ActivityGuildPostsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Post

class GuildPosts : AppCompatActivity() {
    private lateinit var binding: ActivityGuildPostsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuildPostsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.rvGuildPosts.layoutManager = LinearLayoutManager(this)
        binding.rvGuildPosts.adapter = GuildPostAdapter(Post.initSampleData(), this)
    }
}