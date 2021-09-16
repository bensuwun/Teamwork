package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
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
            "isCompleted" to task.completed,
            "tags" to task.tags
        )
    }

    override fun parseDocument(document: DocumentSnapshot): Any {

        return Task(
            document.id, document["name"] as String, document["description"] as String, document["isCompleted"] as Boolean,
            document["about"] as String, document["dueDate"] as Date, document["isSubtask"] as Boolean,
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

    fun getUserTasksFiltered(userId: String, isCompleted: Boolean) {
        this.fireStoreDB
            .collection(this.fireStoreCollection)
            .document(userId)
            .collection(this.taskCollection)
            .whereEqualTo("completed", isCompleted)
            .get()
            .addOnSuccessListener { t ->
                // Prepare intent & bundle
                val taskIntent = Intent(intentLoadedTasks)
                taskIntent.putExtra("taskList", t.toObjects(Task::class.java).toTypedArray())
                broadcastManager.sendBroadcast(taskIntent)
            }
                .addOnFailureListener { t ->
                    Log.e("TaskDAO:getUserTasksFiltered", t.toString())
                }
    }

    fun getProjectTasks(userId: String, projectId: String) {
        this.fireStoreDB
            .collection(this.fireStoreCollection)
            .document(userId)
            .collection("projects")
            .document(projectId)
            .collection(this.taskCollection)
            .get()
            .addOnSuccessListener { t ->
                // Prepare intent & bundle
                val taskIntent = Intent(GET_PROJECT_TASKS_SUCCESS_INTENT)
                taskIntent.putExtra("taskList", t.toObjects(Task::class.java).toTypedArray())
                broadcastManager.sendBroadcast(taskIntent)
            }
            .addOnFailureListener { t ->
                Log.e("TaskDAO:getProjectTasks", t.toString())
                val taskIntent = Intent(GET_PROJECT_TASKS_FAILURE_INTENT)
                broadcastManager.sendBroadcast(taskIntent)
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

    fun createSubtask(userId: String, parentTaskId: String) {
        this.fireStoreDB
            .collection(this.fireStoreCollection)
            .document(userId)
            .collection(this.taskCollection)
            .document(parentTaskId)
            .collection("subtasks")
            .document()
            .set(this.document as Task)
            .addOnSuccessListener {
                val taskIntent = Intent(CREATE_SUBTASK_SUCCESS_INTENT)
                broadcastManager.sendBroadcast(taskIntent)
            }
            .addOnFailureListener {
                val taskIntent = Intent(CREATE_SUBTASK_FAILURE_INTENT)
                broadcastManager.sendBroadcast(taskIntent)
            }
    }

    fun getSubtasks(userId: String, parentTaskId: String) {
        this.fireStoreDB
            .collection(this.fireStoreCollection)
            .document(userId)
            .collection(this.taskCollection)
            .document(parentTaskId)
            .collection("subtasks")
            .get()
            .addOnSuccessListener {
                val taskIntent = Intent(GET_TASK_SUBTASKS_SUCCESS_INTENT)
                taskIntent.putExtra("subtaskList", it.toObjects(Task::class.java).toTypedArray())
                broadcastManager.sendBroadcast(taskIntent)
            }
            .addOnFailureListener {
                val taskIntent = Intent(GET_TASK_SUBTASKS_FAILURE_INTENT)
                broadcastManager.sendBroadcast(taskIntent)
            }
    }

    fun deleteSubtask(subtaskId: String) {
        this.fireStoreDB
            .collectionGroup("subtasks")
            .whereEqualTo(FieldPath.documentId(), subtaskId)

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

    companion object {
        val CREATE_SUBTASK_SUCCESS_INTENT = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.create_subtask_success"
        val CREATE_SUBTASK_FAILURE_INTENT = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.create_subtask_failure"
        val GET_TASK_SUBTASKS_SUCCESS_INTENT = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.get_task_subtask_success"
        val GET_TASK_SUBTASKS_FAILURE_INTENT = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.get_task_subtask_failure"
        val DELETE_SUBTASK_SUCCESS_INTENT = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_subtask_success"
        val DELETE_SUBTASK_FAILURE_INTENT = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_subtask_failure"
        val GET_PROJECT_TASKS_SUCCESS_INTENT = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.get_project_tasks_success"
        val GET_PROJECT_TASKS_FAILURE_INTENT = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.get_project_tasks_failure"
    }
}