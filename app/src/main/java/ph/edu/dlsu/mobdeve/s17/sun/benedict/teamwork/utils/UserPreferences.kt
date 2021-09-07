package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth

/**
 * Custom SharedPreference class to stored logged in user's user ID.
 */
class UserPreferences {

    private lateinit var userPreferences : SharedPreferences
    private val PREFS : String = "userPrefs"

    constructor(context : Context) {
        this.userPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    /**
     * Used to save data into this SharedPreference object.
     */
    fun saveStringPreferences(key : String, value : String){
        val prefsEditor : SharedPreferences.Editor = userPreferences.edit()

        prefsEditor.putString(key, value)
        prefsEditor.apply()
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
        fun getUserAuthUid() : String? {
            val auth = FirebaseAuth.getInstance()
            val loggedInUser = auth.currentUser

            return loggedInUser?.uid
        }
    }
}