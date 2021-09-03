package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.appbar.AppBarLayout
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.ActivityGuildProfileBinding

class GuildProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityGuildProfileBinding
    private lateinit var config : AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGuildProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar : Toolbar = findViewById(R.id.tb_profile)
        setSupportActionBar(toolbar)
        // Display up button from previous fragment
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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