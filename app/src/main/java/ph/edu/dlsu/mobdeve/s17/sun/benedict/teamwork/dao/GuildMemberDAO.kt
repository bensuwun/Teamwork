package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.GuildMember
import java.util.ArrayList

class GuildMemberDAO : TeamworkFirestoreDAO {
    override val fireStoreCollection: String = "guild_members"
    override var queryResults: ArrayList<Any> = ArrayList()
    override var document: Any? = null
    private val TAG : String = "GuildMemberDAO"

    private lateinit var context : Context
    private lateinit var broadcastManager : LocalBroadcastManager
    private val intentMemberCheck : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_member"
    private val intentJoinedGuild : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guild_join_success"
    private val intentLeftGuild : String = "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.left_guild"

    /**
     * Requires context in order to initialize our broadcast manager.
     */
    constructor(context : Context) {
        this.context = context
        this.broadcastManager = LocalBroadcastManager.getInstance(context)
    }

    /**
     * Creates a new document in guild_members collection. Indicates that a user has joined a new guild.
     */
    fun joinGuild(guildId : String, userAuthUid : String) {
        val data = hashMapOf(
            "guildId" to guildId,
            "userAuthUid" to userAuthUid
        )
        val docId = guildId + "_" + userAuthUid
        try{
            // Setup intent and bundle for broadcast
            val intent = Intent(intentJoinedGuild)
            fireStoreDB.collection(fireStoreCollection)
                .document(docId)
                .set(data)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot written with ID: $docId")
                    broadcastManager.sendBroadcast(intent)
                }

                .addOnFailureListener {
                    Log.d(TAG, "Something went wrong when attempting to join the guild")
                    Log.e(TAG, "Something went wrong when attempting to join the guild")
                }
        } catch(e : FirebaseFirestoreException){
            e.message?.let { Log.e(TAG, it) }
            Log.e(TAG, "Something went wrong when calling method joinGuild")
        }
    }

    /**
     * Used when a user leaves a guild. Removes the corresponding document from the guild_members collection.
     */
    fun leaveGuild(guildId : String, userAuthUid: String) {
        val docId = "${guildId}_${userAuthUid}"
        try{
            val intent = Intent(intentLeftGuild)
            fireStoreDB.collection(fireStoreCollection)
                .document(docId)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Successfully deleted guild_member record")
                    broadcastManager.sendBroadcast(intent)
                }
                .addOnFailureListener {
                    Log.e(TAG, "Something went wrong when trying to delete the guild_member record")
                }
        } catch(e : FirebaseFirestoreException) {
            Log.e(TAG, "Something went wrong when trying to delete the guild_member record")
        }
    }

    /**
     * Checks if the given user is a part of a given guild.
     */
    fun isAMemberOf(guildId : String, userAuthUid : String) {
        try{
            // Setup intent and bundle for broadcast
            val intent = Intent(intentMemberCheck)
            val bundle = Bundle()

            val docId = guildId + "_" + userAuthUid
            var isAMember = false
            fireStoreDB.collection(fireStoreCollection)
                .document(docId)
                .get()
                .addOnSuccessListener {
                    // Is already a member
                    if (it.exists()) {
                        isAMember = true
                    }
                    // Send broadcast
                    bundle.putBoolean("isAMember", isAMember)
                    intent.putExtras(bundle)
                    broadcastManager.sendBroadcast(intent)
                }
                .addOnFailureListener {
                    Log.d(TAG, "Something went wrong when calling isAMemberOf method")
                    Log.e(TAG, "Something went wrong when calling isAMemberOf method")
                }
        } catch(e: FirebaseFirestoreException) {
            e.message?.let { Log.e(TAG, it) }
            Log.e(TAG, "Something went wrong when calling method isAMemberOf")
        }
    }

    override fun buildHashMap(): HashMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun parseDocument(document: DocumentSnapshot): GuildMember {
        return GuildMember(document["guildID"] as String, document["userID"] as String)
    }
}