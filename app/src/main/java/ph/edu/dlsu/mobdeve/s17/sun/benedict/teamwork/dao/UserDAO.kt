package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import org.w3c.dom.Document
import java.util.*
import kotlin.collections.HashMap

import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.User

/**
 * Ths user data access object is responsible for reading and writing data
 * from the FireStore database.
 *
 * @author Adriel Isaiah V. Amoguis
 */
class UserDAO(): TeamworkFirestoreDAO() {

    private val TAG = "UserDAO"
    override val fireStoreCollection: String = "users"
    override var queryResults: ArrayList<Any> = ArrayList()
    override var document: Any? = null

    fun createBlankUser(callback: (success: Boolean) -> Unit) {
        this.fireStoreDB
            .collection(this.fireStoreCollection)
            .document((this.document as User).authUid)
            .set(this.document as User)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
                Log.e("UserDAO:createBlankUser", it.localizedMessage)
            }
    }

    /**
     * Get User By Auth Id
     * @description This method is used to get the user data based on the user's Firebase auth ID.
     * @param authId The user's Firebase Auth ID
     * @param callback A callback function that takes in the `success` parameter as a boolean.
     */
    fun getUserByAuthId(authId: String, callback: (success: Boolean) -> Unit) {
        this.queryResults.clear()
        fireStoreDB.collection(fireStoreCollection)
            .document(authId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(
                        TAG,
                        "Got the document with documentId $authId"
                    )
                    this.document = it.result.toObject(User::class.java)
                    callback(true)
                }
                else {
                    Log.e(TAG, "Unable to get document using field $authId.")
                    callback(false)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.message.toString())
                callback(false)
            }
    }

    /**
     * Build Hash Map
     * @description This method generates the Java HashMap that firebase expects
     * as the document for the firestore database.
     * @return HashMap<String, Any> A reference to this class' HashMap.
     */
    override fun buildHashMap(): HashMap<String, Any> {
        val userMap = HashMap<String, Any>()
        (document as User).let { userMap.put("username", it.username) }
        (document as User).let { userMap.put("profileImage", it.profileImage) }

        return userMap
    }

    /**
     * Parse User Document
     * @description This method converts the Firestore Document Map
     * into a User object.
     * @param document - The DocumentSnapshot from firestore.
     * @return User
     */
    override fun parseDocument(document: DocumentSnapshot): User {

        // Convert the Arrays of Reference Fields into Actual ArrayLists
        val projectReferences = ArrayList<DocumentReference>()
        val taskReferences = ArrayList<DocumentReference>()

        if(document["projects"] != null) {
            for(docRef in (document["projects"] as List<*>)) {
                projectReferences.add(docRef as DocumentReference)
            }
        }

        if(document["tasks"] != null) {
            for(docRef in (document["tasks"] as List<*>)) {
                taskReferences.add(docRef as DocumentReference)
            }
        }

        return User(
            document["username"] as String,
            document["profileImage"] as String,
            document["authUid"] as String,
        )
    }
}
