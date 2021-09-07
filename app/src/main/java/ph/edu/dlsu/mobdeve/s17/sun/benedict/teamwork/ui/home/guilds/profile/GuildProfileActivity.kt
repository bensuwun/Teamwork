package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.appbar.AppBarLayout
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildMemberDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.ActivityGuildProfileBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences

class GuildProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGuildProfileBinding
    private val TAG : String = "GuildProfileActivity"
    private lateinit var config : AppBarConfiguration

    // Guild Information
    private lateinit var guildId : String
    private lateinit var name : String
    private var memberCount : Long = 0
    private lateinit var description : String
    private lateinit var guildImg : String
    private lateinit var guildHeader : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuildProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set basic guild information
        try{
            guildId = intent.getStringExtra("guildId").toString()
            name = intent.getStringExtra("name").toString()
            memberCount = intent.getLongExtra("member_count", 0)
            description = intent.getStringExtra("description").toString()
        } catch(e : Error) {
            e.message?.let { Log.e(TAG, it) }
            Log.e(TAG, "Something went wrong when setting guild profile information. " +
                    "Make sure you passed the guildId, name, member_count, and description when navigating to this Activity")
        }

        val toolbar : Toolbar = findViewById(R.id.tb_profile)
        toolbar.title = name
        setSupportActionBar(toolbar)
        // Display up button from previous fragment
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Update views
        binding.tvGuildName.text = name
        // binding.tvGuildMembers.text = member_count.toString()
        // binding.tvGuildDescription.text = description
        UserPreferences.getUserAuthUid()?.let { GuildMemberDAO().isAMemberOf(guildId, it) }

        setEventHandlers()
    }

    private fun setEventHandlers(){
        var isShow = true
        var scrollRange = -1
        binding.guildAppBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { barLayout, verticalOffset ->
            if (scrollRange == -1){
                scrollRange = barLayout?.totalScrollRange!!
            }
            if (scrollRange + verticalOffset == 0){
                binding.collapsingtlProfileHeader.title = "Guild Name"
                isShow = true
            } else if (isShow){
                binding.collapsingtlProfileHeader.title = " " //careful there should a space between double quote otherwise it wont work
                isShow = false
            }
        })

        // Handles joining/leaving the guild
        binding.btnAction.setOnClickListener {
            // TODO: Determine if the user has already joined the guild or not
            // JOIN
            // Update guild member_count
            GuildDAO().incrementGuildMemberCount(guildId)

            // Add new document in guild_members collection
            UserPreferences.getUserAuthUid()?.let { it1 ->
                GuildMemberDAO().joinGuild(guildId,
                    it1
                )
            }

            // TODO: (3) Add toast when successfully joined -> navigate to dashboard

        }
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