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
    private val tag : String = "GuildMemberDAO"

    override fun buildHashMap(): HashMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun parseDocument(document: DocumentSnapshot): GuildMember {
        return GuildMember(document["guildID"] as String, document["userID"] as String)
    }

    /**
     * Obtains all guild IDs that the given user has joined.
     */
    fun getMyGuildIDs(userID : String) : ArrayList<String> {
        var guildIDs : ArrayList<String> = ArrayList()
        GlobalScope.launch(Dispatchers.IO) {
            try{
                val result = fireStoreDB.collection(fireStoreCollection).whereEqualTo("userID", userID)
                    .get()
                    .await()
                for (document in result){
                    Log.d(tag, "${document.id} => ${document.data}")
                    guildIDs.add(parseDocument(document).guildID)
                }
            } catch (e: FirebaseFirestoreException) {
                Log.w(tag, "Error getting documents", e)
            }
        }

        return guildIDs
    }
}