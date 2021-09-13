package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao

import android.content.Context
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.firestore.FirebaseFirestore
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task

class TaskDAO(ctx: Context) {
    // A sub-collection implementation of the DAO

    var broadcastManager: LocalBroadcastManager = LocalBroadcastManager.getInstance(ctx)
    val firestore = FirebaseFirestore.getInstance()
    var task: Task? = null

    

}