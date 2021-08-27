package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.authentication

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.UserDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.User
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.HomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var fbAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.fbAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = this.fbAuth.currentUser
        if(currentUser != null && !currentUser.isAnonymous && currentUser.isEmailVerified) {
            // Skip login/register page
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
        else {
            Toast.makeText(applicationContext, "Not logged in", Toast.LENGTH_LONG).show()
        }
    }
}