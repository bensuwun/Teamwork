package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao

import android.util.Log
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

class GuildMemberDAO() : TeamworkFirestoreDAO() {
    override val fireStoreCollection: String = "guild_members"
    override var queryResults: ArrayList<Any> = ArrayList()
    override var document: Any? = null
    private val TAG : String = "GuildMemberDAO"

    override fun buildHashMap(): HashMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun parseDocument(document: DocumentSnapshot): GuildMember {
        return GuildMember(document["guildID"] as String, document["userID"] as String)
    }


    /**
     * Creates a new document in guild_members collection. Indicates that a user has joined a new guild.
     */
    fun joinGuild(guildId : String, userAuthUid : String) {
        val data = hashMapOf(
            "guildId" to guildId,
            "userAuthUid" to userAuthUid
        )
        fireStoreDB.collection(fireStoreCollection)
            .add(data)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot written with ID: ${it.id}")
            }

            .addOnFailureListener {
                Log.d(TAG, "Something went wrong went attempting to join the guild")
                Log.e(TAG, "Something went wrongwent attempting to join the guild")
            }
    }

    /**
     * Checks if the given user is a part of a given guild.
     */
    fun isAMemberOf(guildId : String, userAuthUid : String) {
        fireStoreDB.collection(fireStoreCollection)
            .whereEqualTo("guildId", guildId)
            .whereEqualTo("userAuthUid", userAuthUid)
            .get()
            .addOnSuccessListener {
                // NOT a member
                if (it.size() == 0) {
                    // TODO: Broadcast
                }
                // IS a member
                else if (it.size() == 1) {
                    // TODO: Broadcast
                }
                else {
                    Log.w(TAG, "WARNING: Data duplicate detected in guild_members collection")
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Something went wrong when calling isAMemberOf method")
                Log.e(TAG, "Something went wrong when calling isAMemberOf method")
            }
    }
}