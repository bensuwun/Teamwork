package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Comment
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Post
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Tags
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.posts.AddGuildPostFragment
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.posts.ViewPostFragment
import java.util.ArrayList
import kotlin.reflect.typeOf

class PostDAO(context : Context) : TeamworkFirestoreDAO() {
    override val fireStoreCollection: String = "guilds"
    private val postsCollection: String = "posts"
    private val commentsCollection: String = "comments"
    override var queryResults: ArrayList<Any> = ArrayList()
    override var document: Any? = null
    private val TAG : String = "PostDAO"

    // Broadcast variables
    private val intentPostsLoaded : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_posts_loaded"
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
    fun addGuildPost(guildId : String, data : Post) {
        val intent = Intent(AddGuildPostFragment.intentPostAdded)
        try{
            fireStoreDB.collection(fireStoreCollection)
                .document(guildId)
                .collection(postsCollection)
                .add(data)
                .addOnSuccessListener {
                    broadcastManager.sendBroadcast(intent)
                }

        } catch (e : FirebaseFirestoreException) {
            Log.e(TAG, e.message.toString())
        }
    }

    /**
     * UPDATE: UNUSED, integrated data fetching inside ViewPostFragment for realtime updates
     * Gets the comments of a given post in a guild.
     * Guild -> Post -> Comments
     */
    fun getComments(guildId : String, postId : String) {
        val intent = Intent(ViewPostFragment.intentCommentsLoaded)
        val bundle = Bundle()
        val comments : ArrayList<Comment> = ArrayList()
        try {
            fireStoreDB.collection(fireStoreCollection)
                .document(guildId)
                .collection(postsCollection)
                .document(postId)
                .collection(commentsCollection)
                    /*
                .addSnapshotListener { value, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    Log.d(TAG, "Retrieving comments")
                    for (document in value!!) {
                        Log.d(TAG, "${document.id}: ${document.data}")
                        comments.add(document.toObject(Comment::class.java))
                    }
                    bundle.putParcelableArrayList("comments", comments)
                    intent.putExtras(bundle)
                    broadcastManager.sendBroadcast(intent)
                }
                     */
                .get()
                .addOnSuccessListener {
                    Log.d(TAG, "Comments retrieved")
                    for(document in it.documents) {
                        Log.d(TAG, "${document.id}: ${document.data}")
                        document.toObject(Comment::class.java)?.let { it1 -> comments.add(it1) }
                    }
                    bundle.putParcelableArrayList("comments", comments)
                    intent.putExtras(bundle)
                    broadcastManager.sendBroadcast(intent)
                }


        } catch(e: FirebaseFirestoreException) {
            Log.e(TAG, e.message.toString())
        }
    }

    fun addComment(guildId : String, postId : String, data : Comment) {
        val intent = Intent(ViewPostFragment.intentCommentAdded)
        val bundle = Bundle()
        try {
            fireStoreDB.collection(fireStoreCollection)
                .document(guildId)
                .collection(postsCollection)
                .document(postId)
                .collection(commentsCollection)
                .add(data)
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully added comment")
                    bundle.putString("docId", it.id)
                    intent.putExtras(bundle)
                    fireStoreDB.collection(fireStoreCollection)
                        .document(guildId)
                        .collection(postsCollection)
                        .document(postId)
                        .update("comments", FieldValue.increment(1))
                        .addOnSuccessListener {
                            broadcastManager.sendBroadcast(intent)
                        }
                }
        } catch(e: FirebaseFirestoreException) {
            Log.e(TAG, e.message.toString())
        }
    }

    override fun buildHashMap(): HashMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun parseDocument(document: DocumentSnapshot): Any {
        TODO("Not yet implemented")
    }
}