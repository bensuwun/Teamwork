package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.firestore.DocumentSnapshot
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Project
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task
import java.util.ArrayList

class ProjectDAO(ctx: Context) : TeamworkFirestoreDAO() {
    override fun buildHashMap(): HashMap<String, Any> {
        TODO("Not needed")
    }

    override fun parseDocument(document: DocumentSnapshot): Any {
        TODO("Not needed")
    }

    override val fireStoreCollection = "users"
    val projectsCollection = "projects"
    override var document: Any? = null
    override var queryResults: ArrayList<Any> = ArrayList()

    // Broadcast Manager
    private var broadcastManager : LocalBroadcastManager = LocalBroadcastManager.getInstance(ctx)

    // DAO Methods for Projects
    fun createProject(userId: String) {
        this.fireStoreDB
            .collection(this.fireStoreCollection)
            .document(userId)
            .collection(this.projectsCollection)
            .document()
            .set(this.document as Project)
            .addOnSuccessListener {
                val broadcastIntent = Intent(CREATE_PROJECT_SUCCESS_INTENT)
                this.broadcastManager.sendBroadcast(broadcastIntent)
            }
            .addOnFailureListener {
                Log.e("ProjectDAO:createProject", it.localizedMessage)
                val broadcastIntent = Intent(CREATE_PROJECT_FAILURE_INTENT)
                this.broadcastManager.sendBroadcast(broadcastIntent)
            }
    }

    fun getAllUserProjects(userId: String) {
        this.fireStoreDB
            .collection(this.fireStoreCollection)
            .document(userId)
            .collection(this.projectsCollection)
            .get()
            .addOnSuccessListener { t ->
                // Prepare intent & bundle
                val projectIntent = Intent(GET_USER_PROJECTS_SUCCESS_INTENT)
                projectIntent.putExtra("projectList", t.toObjects(Project::class.java).toTypedArray())
                broadcastManager.sendBroadcast(projectIntent)
            }
            .addOnFailureListener {
                Log.e("ProjectDAO:getAllUserProjects", it.localizedMessage)
                val broadcastIntent = Intent(GET_USER_PROJECTS_FAILURE_INTENT)
                this.broadcastManager.sendBroadcast(broadcastIntent)
            }
    }

    fun updateProjectCb(userId: String, callback: (success: Boolean) -> Unit) {
        this.fireStoreDB
            .collection(this.fireStoreCollection)
            .document(userId)
            .collection(this.projectsCollection)
            .document((this.document as Project).projectId)
            .set(this.document as Project)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                Log.e("ProjectDAO:updateProjectCb", it.localizedMessage)
                callback(false)
            }
    }

    fun deleteProjectCb(userId: String, projectId: String, callback: (success: Boolean) -> Unit) {
        this.fireStoreDB
            .collection(this.fireStoreCollection)
            .document(userId)
            .collection(this.projectsCollection)
            .document(projectId)
            .delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // Static Attributes
    companion object {
        val CREATE_PROJECT_SUCCESS_INTENT = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.create_project_success"
        val CREATE_PROJECT_FAILURE_INTENT = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.create_project_failure"
        val GET_USER_PROJECTS_SUCCESS_INTENT = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.get_user_projects_success"
        val GET_USER_PROJECTS_FAILURE_INTENT = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.get_user_projects_failure"
    }
}