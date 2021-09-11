package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Post
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Tags
import java.util.ArrayList
import kotlin.reflect.typeOf

class PostDAO(context : Context) : TeamworkFirestoreDAO() {
    override val fireStoreCollection: String = "guilds"
    private val postsCollection: String = "posts"
    override var queryResults: ArrayList<Any> = ArrayList()
    override var document: Any? = null
    private val TAG : String = "PostDAO"

    // Broadcast variables
    private var context : Context = context
    private val intentPostsLoaded : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_posts_loaded"
    private val intentPostAdded : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_posts_added"
    private var broadcastManager : LocalBroadcastManager = LocalBroadcastManager.getInstance(context)

    /**
     * Get guild posts of a given guild
     */
    fun getGuildPosts(guildId: String) {
        val intent = Intent(intentPostsLoaded)
        val bundle = Bundle()
        var posts : ArrayList<Post> = ArrayList()
        try{
            fireStoreDB.collection(fireStoreCollection)
                .document(guildId)
                .collection("posts")
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, "Querying guild posts")
                    for(document in it.documents) {
                        Log.d(TAG, "ID: ${document.id} | Data: ${document.data}")
                        document.toObject(Post::class.java)?.let { it1 -> posts.add(it1) }
                    }

                    bundle.putParcelableArrayList("posts", posts)
                    intent.putExtras(bundle)
                    broadcastManager.sendBroadcast(intent)
                }
                .addOnFailureListener {
                    Log.e(TAG, "Something went wrong when querying the guild posts")
                }
        } catch(e : FirebaseFirestoreException) {
            e.message?.let { Log.e(TAG, it) }
        }
    }

    /**
     * Used when user attempts to post in a guild.
     */
    /*
    fun addGuildPost(guildId : String, data : Post) {
        val intent = Intent(intentPostAdded)
        try{
            fireStoreDB.collection(fireStoreCollection)
                .document(guildId)
                .collection(postsCollection)

        }
    }

     */

    override fun buildHashMap(): HashMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun parseDocument(document: DocumentSnapshot): Any {
        TODO("Not yet implemented")
    }
}