package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*
import kotlin.collections.HashMap

import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Project
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.User
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task

/**
 * Ths user data access object is responsible for reading and writing data
 * from the FireStore database.
 *
 * @author Adriel Isaiah V. Amoguis
 */
class UserDAO(): TeamworkFirestoreDAO() {

    override val fireStoreCollection: String = "users"
    override var queryResults: ArrayList<Any> = ArrayList()
    override var document: Any? = null

    /**
     * Build Hash Map
     * @description This method generates the Java HashMap that firebase expects
     * as the document for the firestore database.
     * @return HashMap<String, Any> A reference to this class' HashMap.
     */
    override fun buildHashMap(): HashMap<String, Any> {
        val userMap = HashMap<String, Any>()
        (document as User)?.let { userMap.put("username", it.username) }
        (document as User)?.let { userMap.put("emailAddress", it.emailAddress) }
        (document as User)?.let { userMap.put("profileImage", it.profileImageUri) }
        (document as User)?.let { userMap.put("projects", it.projects) }
        (document as User)?.let { userMap.put("tasks", it.tasks) }

        return userMap
    }

    /**
     * Parse User Document
     * @description This method converts the Firestore Document Map
     * into a User object.
     * @param document - The DocumentSnapshot from firestore.
     * @return User
     */
    override fun parseDocument(document: DocumentSnapshot): User {

        // Convert Reference Fields in to Actual ArrayLists
        val projectList = ArrayList<Project>()
        val taskList = ArrayList<Task>()



        return User(
            document.id,
            document["username"] as String,
            document["emailAddress"] as String,
            document["profileImage"] as String,
            projectList,
            taskList
        )
    }
}
