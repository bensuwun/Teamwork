package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    private lateinit var appBarConfig : AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavView = binding.bottomNavigationView

        // FragmentContainerView is currently not friendly, so we need to use supportFragmentManager to obtain the fragment
        // as a NavHostFragment, then gets its navController
        // https://stackoverflow.com/questions/50502269/illegalstateexception-link-does-not-have-a-navcontroller-set
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.home_nav_controller) as NavHostFragment
        appBarConfig = AppBarConfiguration(setOf(R.id.tasksFragment, R.id.projectsFragment, R.id.guildsFragment, R.id.settingsFragment))
        setupActionBarWithNavController(navHostFragment.navController, appBarConfig)

        bottomNavView.setupWithNavController(navHostFragment.navController)
    }

    // Override the "Up" button to support our navController
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.home_nav_controller)
        return navController.navigateUp(appBarConfig)
                || super.onSupportNavigateUp()
    }
}