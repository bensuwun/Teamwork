package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.ParcelableDocumentReference
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task
import java.util.*
import kotlin.collections.HashMap

class TaskDAO(ctx: Context): TeamworkFirestoreDAO() {

    // Implementation Attributes
    override val fireStoreCollection: String = "users"
    private val taskCollection: String = "tasks"
    override var queryResults: ArrayList<Any> = ArrayList()
    override var document: Any? = null

    // Broadcaster Variables
    private val intentLoadedTasks : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.all_user_tasks_loaded"
    private val intentCreatedTask : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.created_user_task"
    private val intentFailedTaskCreate : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.create_user_task_failed"
    private val intentDeleteTaskSuccess : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_user_task"
    private val intentDeleteTaskFailure : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_user_task_failed"
    private val intentUpdateTaskSuccess : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.update_user_task"
    private val intentUpdateTaskFailure : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.update_user_task_failed"
    private var broadcastManager : LocalBroadcastManager = LocalBroadcastManager.getInstance(ctx)

    override fun buildHashMap(): HashMap<String, Any> {
        val task: Task = this.document as Task
        return hashMapOf(
            "name" to task.name,
            "about" to task.about,
            "description" to task.description,
            "dueDate" to task.dueDate,
            "isCompleted" to task.isCompleted,
            "tags" to task.tags
        )
    }

    override fun parseDocument(document: DocumentSnapshot): Any {

        val subtaskParcelableReferences = ArrayList<ParcelableDocumentReference>()
        if(document["subtasks"] != null) {
            for(docRef in (document["subtasks"] as List<*>)) {
                subtaskParcelableReferences.add(ParcelableDocumentReference(docRef as DocumentReference))
            }
        }

        return Task(
            document.id, document["name"] as String, document["description"] as String, document["isCompleted"] as Boolean,
            document["about"] as String, document["dueDate"] as Date, subtaskParcelableReferences,
            document["tags"] as ArrayList<String>
        )
    }

    // Firestore Methods
    fun getAllUserTasks(userId: String) {
        this.fireStoreDB
            .collection(this.fireStoreCollection)
            .document(userId)
            .collection(this.taskCollection)
            .get()
            .addOnSuccessListener { t ->
                // Prepare intent & bundle
                val taskIntent = Intent(intentLoadedTasks)
                taskIntent.putExtra("taskList", t.toObjects(Task::class.java).toTypedArray())
                broadcastManager.sendBroadcast(taskIntent)
            }
            .addOnFailureListener { t ->
                Log.e("TaskDAO:getAllUserTasks", t.toString())
            }
    }

    fun createNewTask(userId: String) {
        this.fireStoreDB
            .collection(this.fireStoreCollection)
            .document(userId)
            .collection(this.taskCollection)
            .document()
            .set(this.document as Task)
            .addOnSuccessListener { t ->
                val taskIntent = Intent(intentCreatedTask)
                broadcastManager.sendBroadcast(taskIntent)
            }
            .addOnFailureListener { e ->
                Log.e("TaskDAO:createNewTask", e.toString())
                val taskIntent = Intent(intentFailedTaskCreate)
                broadcastManager.sendBroadcast(taskIntent)
            }
    }

    fun deleteTask(userId: String) {
        this.fireStoreDB
            .collection(this.fireStoreCollection)
            .document(userId)
            .collection(this.taskCollection)
            .document((this.document as Task).taskId)
            .delete()
            .addOnSuccessListener {
                val taskIntent = Intent(intentDeleteTaskSuccess)
                broadcastManager.sendBroadcast(taskIntent)
            }
            .addOnFailureListener { e ->
                Log.e("TaskDAO:deleteTask", e.toString())
                val taskIntent = Intent(intentDeleteTaskFailure)
                broadcastManager.sendBroadcast(taskIntent)
            }
    }

    fun updateTask(userId: String) {
        this.fireStoreDB
            .collection(this.fireStoreCollection)
            .document(userId)
            .collection(this.taskCollection)
            .document((this.document as Task).taskId)
            .set(this.document as Task)
            .addOnSuccessListener {
                val taskIntent = Intent(intentUpdateTaskSuccess)
                broadcastManager.sendBroadcast(taskIntent)
            }
            .addOnFailureListener { e ->
                Log.e("TaskDAO:updateTask", e.toString())
                val taskIntent = Intent(intentUpdateTaskFailure)
                broadcastManager.sendBroadcast(taskIntent)
            }
    }
}