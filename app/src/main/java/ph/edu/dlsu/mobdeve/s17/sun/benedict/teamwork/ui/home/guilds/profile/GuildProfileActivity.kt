package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.profile

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.appbar.AppBarLayout
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.GuildMemberDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.ActivityGuildProfileBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
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

    private val intentMemberCheck = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_member"
    private val intentJoinedGuild = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_join_success"
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, "Broadcast Received")
            if (intent != null) {
                when(intent.action){
                    intentMemberCheck -> {
                        var bundle = intent?.extras
                        var isAMember = bundle?.getBoolean("isAMember")
                        if (isAMember == true){
                            binding.btnAction.visibility = View.GONE
                        }
                    }
                    intentJoinedGuild -> {
                        Toast.makeText(context, "Successfully joined this guild!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuildProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Register broadcast receiver
        val filter = IntentFilter()
        filter.addAction(intentMemberCheck)
        filter.addAction(intentJoinedGuild)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter)


        // Set basic guild information
        try{
            guildId = intent.getStringExtra("guildId").toString()
            name = intent.getStringExtra("name").toString()
            memberCount = intent.getLongExtra("member_count", 0)
            description = intent.getStringExtra("description").toString()
            UserPreferences.getUserAuthUid()?.let {
                GuildMemberDAO(this).isAMemberOf(guildId,
                    it
                )
            }
        } catch(e : Error) {
            e.message?.let { Log.e(TAG, it) }
            Log.e(TAG, "Something went wrong when setting guild profile information. " +
                    "Make sure you passed the guildId, name, member_count, and description when navigating to this Activity")
        }


        // Initialize toolbar
        initToolbar()

        // Update views
        binding.tvGuildName.text = name
        binding.tvGuildMembers.text = getString(R.string.member_count, memberCount)
        binding.tvGuildDescription.text = description

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
            // Set document in guild_members collection
            UserPreferences.getUserAuthUid()?.let { it1 ->
                GuildMemberDAO(this).joinGuild(guildId,
                    it1
                )
            }
            // Increment guild member count
            GuildDAO(this).incrementGuildMemberCount(guildId)
        }
    }

    private fun initToolbar() {
        val toolbar : Toolbar = findViewById(R.id.tb_profile)
        setSupportActionBar(toolbar)
        supportActionBar?.title = name

        // Display up button from previous fragment
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

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }
}