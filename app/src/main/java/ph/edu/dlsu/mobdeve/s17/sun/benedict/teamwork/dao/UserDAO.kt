package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao

import com.google.firebase.firestore.FirebaseFirestore
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Project
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.User
import java.util.*
import kotlin.collections.HashMap

/**
 * Ths user data access object is responsible for reading and writing data
 * from the FireStore database.
 *
 * @author Adriel Isaiah V. Amoguis
 */
class UserDAO {

    private val fireStoreDB = FirebaseFirestore.getInstance()
    private val fireStoreCollection = "users"
    val queryResults = ArrayList<User>();

    // Instance attributes
    private var userHashMap: HashMap<String, Any> = HashMap()
    var userId: String = ""
    var username: String = ""
    var emailAddress: String = ""
    private var profileImage = ""
    private val projects = ArrayList<Project>()
    private val tasks = ArrayList<Task>()

    // Build Hashmap
    fun buildHashMap(): HashMap<String, Any> {
        this.userHashMap.clear()
        this.userHashMap.put("username", this.username)
        this.userHashMap.put("emailAddress", this.emailAddress)
        this.userHashMap.put("profileImage", this.profileImage)
        this.userHashMap.put("projects", this.projects)
        this.userHashMap.put("tasks", this.tasks)
        return this.userHashMap
    }

    // Get HashMap
    fun getHashMap(): HashMap<String, Any> {
        return this.userHashMap
    }

    // Firestore Operations
    fun getUserById() {
        // TODO
    }

    fun createNewUser() {
        // TODO
    }

    fun deleteUser() {
        // TODO
    }

    fun updateUser() {
        // TODO
    }


}
