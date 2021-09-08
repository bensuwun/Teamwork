package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.firestore.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Document
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.GuildMember
import java.util.ArrayList

class GuildDAO() : TeamworkFirestoreDAO() {
    override val fireStoreCollection: String = "guilds"
    override var queryResults: ArrayList<Any> = ArrayList()
    override var document: Any? = null
    private val TAG : String = "GuildDAO"
    private lateinit var context : Context
    private lateinit var broadcastManager : LocalBroadcastManager

    /**
     * Requires context in order to initialize our broadcast manager.
     */
    constructor(context : Context) : this() {
        this.context = context
        this.broadcastManager = LocalBroadcastManager.getInstance(context)
    }

    /**
     * Build Hash Map
     * @description This method generates the Java HashMap that firebase expects
     * as the document for the firestore database.
     * @return HashMap<String, Any> A reference to this class' HashMap.
     */
    override fun buildHashMap(): HashMap<String, Any> {
        TODO("Not yet implemented")
    }

    /**
     * Parse Guild Document
     * @description This method converts the Firestore Document Map
     * into a Guild object.
     * @param Guild - The DocumentSnapshot from firestore.
     * @return Guild
     */
    override fun parseDocument(document: DocumentSnapshot): Guild {
        val description : String = document["description"] as String
        val memberCount : Long = document["member_count"] as Long
        val name : String = document["name"] as String

        return Guild()
    }

    /**
     * This function queries all the documents in the "guilds" collection in Firestore.
     * After query, it broadcasts the queried guilds to SearchGuilds.kt
     */
    fun getAllGuilds(userAuthUid : String){
        queryResults.clear()

        var guildIDs : ArrayList<String> = ArrayList()
        try{
            // Setup intent and bundle for broadcast
            val intent = Intent("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.guilds")
            val bundle = Bundle()

            fireStoreDB.collection(fireStoreCollection)
                .get()
                .addOnCompleteListener{
                    if (it.isSuccessful){
                        Log.d(TAG,"Successfully queried all guilds")
                        for (document in it.result) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            queryResults.add(document.toObject(Guild::class.java))
                        }
                        bundle.putParcelableArrayList("guilds", queryResults as ArrayList<Guild>)
                        intent.putExtras(bundle)
                        broadcastManager.sendBroadcast(intent)
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.w(TAG, "Error getting guild documents", e)
        }
    }

    fun getMyGuilds(userAuthUid: String){
        queryResults.clear()

        var guildIDs : ArrayList<String> = ArrayList()
        try{
            // Setup bundle and intent for broadcast
            val intent = Intent("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.my_guilds")
            val bundle = Bundle()

            // Get guild IDs
            var result = fireStoreDB.collection("guild_members").whereEqualTo("userAuthUid", userAuthUid)
                .get()
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        Log.d(TAG, "Successfully queried user's related guildIDs")
                        for(document in it.result){
                            Log.d(TAG, "${document.id} => ${document.data}")
                            guildIDs.add(document.toObject(GuildMember::class.java).guildId)
                        }
                        Log.d(TAG, "Size: " + guildIDs.size)

                        if (guildIDs.size > 0){
                            Log.d(TAG, "Sending query for all guilds not joined by user")
                            // Query guilds unrelated to userid
                            fireStoreDB.collection(fireStoreCollection)
                                .whereIn("__name__", guildIDs)
                                .get()
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Log.d(
                                            TAG,
                                            "Successfully queried all guilds not joined by user"
                                        )
                                        for (document in it.result) {
                                            Log.d(TAG, "${document.id} => ${document.data}")
                                            queryResults.add(document.toObject(Guild::class.java))
                                        }

                                        // Broadcast the list of guilds (queryResults) to SearchGuilds
                                        Log.d(TAG, "Broadcasting queried guilds")
                                        for (guild in queryResults) {
                                            bundle.putParcelableArrayList(
                                                "guilds",
                                                queryResults as ArrayList<Guild>
                                            )
                                        }
                                        intent.putExtras(bundle)
                                        broadcastManager.sendBroadcast(intent)
                                    }
                                }
                        }

                        else{
                            bundle.putParcelableArrayList("guilds", ArrayList())
                            intent.putExtras(bundle)
                            broadcastManager.sendBroadcast(intent)
                        }
                    }
                }
        } catch (e: FirebaseFirestoreException) {
            Log.w(TAG, "Error getting guild documents", e)
        }
    }

    fun incrementGuildMemberCount(guildId : String) {
        fireStoreDB.collection(fireStoreCollection)
            .document(guildId)
            .update("member_count", FieldValue.increment(1))
    }
}