package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.UserDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.ActivityHomeBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.User
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.authentication.MainActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    private lateinit var appBarConfig : AppBarConfiguration

    lateinit var activeUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set toolbar
        val toolbar : Toolbar = findViewById(R.id.tb_home)
        setSupportActionBar(toolbar)

        val bottomNavView = binding.bottomNavigationView

        // Get User Identifier from Firebase Auth
         val fireBaseUser = FirebaseAuth.getInstance().currentUser
        if(fireBaseUser == null) {
            // Go back to MainActivity
            val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(mainActivityIntent)
            finish()
        }

        // Get User Data from Firestore
        val userDAO = UserDAO()
        if(fireBaseUser != null)
            userDAO.getUserByAuthId(fireBaseUser!!.uid) { success ->
                if(!success) {
                    Toast.makeText(this.applicationContext, "An error occurred that logged you out.", Toast.LENGTH_LONG).show()
                    // Sign out from Firebase
                    FirebaseAuth.getInstance().signOut()
                    // Sign out from Google
                    GoogleSignIn.getClient(applicationContext,
                        GoogleSignInOptions.Builder(
                        GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut()
                    // Go back to MainActivity
                    val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(mainActivityIntent)
                    finish()
                } else {
                    // Set user data
                    this.activeUser = userDAO.document as User
                }
            }


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