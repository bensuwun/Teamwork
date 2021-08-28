package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.posts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.GuildPostAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.ActivityGuildPostsBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Post

class GuildPostsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGuildPostsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuildPostsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar : Toolbar = findViewById(R.id.toolbar_posts)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    // Override the up button to go back to previous calling fragment
    override fun onOptionsItemSelected(item : MenuItem) : Boolean {
        super.onOptionsItemSelected(item)

        when (item.itemId) {
            android.R.id.home -> finish()
        }

        return true
    }
}