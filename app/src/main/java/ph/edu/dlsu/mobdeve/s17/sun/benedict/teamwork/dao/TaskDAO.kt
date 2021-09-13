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
    private var broadcastManager : LocalBroadcastManager = LocalBroadcastManager.getInstance(ctx)

    override fun buildHashMap(): HashMap<String, Any> {
        val task: Task = this.document as Task
        return hashMapOf(
            "name" to task.name,
            "about" to task.about,
            "description" to task.description,
            "dueDate" to task.dueDate,
            "isCompleted" to task.isFinished,
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
            document.id, document["name"] as String, document["description"] as String,
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
}