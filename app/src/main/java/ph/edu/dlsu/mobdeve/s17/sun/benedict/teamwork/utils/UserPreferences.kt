package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils

import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.UserDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.User

/**
 * Custom SharedPreference class to stored logged in user's user ID.
 */
class UserPreferences(val context: Context) {
    private val PREFS : String = "userPrefs"
    private var userPreferences : SharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private val loggedInUserKey : String = "LoggedInUser"

    /**
     * Used to save data into this SharedPreference object.
     */
    fun saveStringPreferences(key : String, value : String){
        val prefsEditor : SharedPreferences.Editor = userPreferences.edit()

        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    /**
     * For saving the logged in User object to SharedPreference.
     */
    fun saveLoggedInUser(user : User) {
        val prefsEditor = userPreferences.edit()

        // For saving objects to SharedPreferences
        val gson = Gson()
        val json : String = gson.toJson(user)
        prefsEditor.putString(loggedInUserKey, json)
        prefsEditor.apply()
    }

    fun getLoggedInUser() : User? {
        var user : User? = null
        val gson = Gson()
        val json : String? = userPreferences.getString(loggedInUserKey, "")
        if (json != ""){
            user = gson.fromJson(json, User::class.java)
        }

        return user
    }

    /**
     * Method to obtain user preference.
     */
    fun getStringPreferences(key : String): String? {
        return (userPreferences.getString(key, "Nothing saved"))
    }

    /**
     * Static variables including "key" name for userId
     */
    companion object {
        const val userIdKey = "userId"
        private var loggedInUser : User? = null
        fun getUserAuthUid() : String? {
            val auth = FirebaseAuth.getInstance()
            val loggedInUser = auth.currentUser

            return loggedInUser?.uid
        }
    }
}