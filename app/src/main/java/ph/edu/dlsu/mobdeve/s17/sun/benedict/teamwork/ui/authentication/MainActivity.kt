package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.authentication

import android.content.ContentValues.TAG
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
        if(currentUser != null) {
            // Skip login/register page
            // Skip this activity
        }
        val userDAO = UserDAO()
        val lambda = { _: Boolean ->
            val user = userDAO.document as User
            Toast.makeText(applicationContext, user.toString(), Toast.LENGTH_LONG).show()
        }
        userDAO.getDocumentById("KZpVJ7lrdSO2DMSpm9nK", lambda)
    }
}