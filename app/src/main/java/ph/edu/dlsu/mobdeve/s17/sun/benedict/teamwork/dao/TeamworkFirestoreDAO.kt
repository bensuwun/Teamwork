package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.util.ArrayList

/**
 * This abstract data access object is responsible for reading and writing data
 * from the FireStore database.
 *
 * @author Adriel Isaiah V. Amoguis
 */
abstract class TeamworkFirestoreDAO() {

    // Firestore
    private val fireStoreDB = FirebaseFirestore.getInstance()
    abstract val fireStoreCollection: String

    // Query Results
    abstract var queryResults: ArrayList<Any>
    abstract var document: Any?

    // Abstract Constructor
    /**
     * This constructor sets the user object.
     * This constructor should be used
     * @param dataInstance
     */
    constructor(dataInstance: Any): this() {
        this.document = dataInstance
    }

    /**
     * Build Hash Map
     * @description This method generates the Java HashMap that firebase expects
     * as the document for the firestore database.
     * @return HashMap<String, Any> A reference to this class' HashMap.
     */
    abstract fun buildHashMap(): HashMap<String, Any>

    /**
     * Parse User Document
     * @description This method converts the Firestore Document Map
     * into a User object.
     * @param document - The DocumentSnapshot from firestore.
     * @return User
     */
    abstract fun parseDocument(document: DocumentSnapshot): Any

    /**
     * Get Document By Id
     * @descrption Returns a document to this DAO based on a given user documentID.
     * @return Task coroutine that resolves once the user data is available.
     */
    fun getDocumentById(documentId: String, callback: (success: Boolean) -> Unit) {
        fireStoreDB.collection(fireStoreCollection)
            .document(documentId)
            .get()
            .addOnCompleteListener { document ->
                val data = document.result
                if(data != null) {
                    Log.d(ContentValues.TAG, "Got document!")
                    val dataObject = parseDocument(data)
                    this.document = dataObject
                    callback(true)
                } else {
                    Log.d(ContentValues.TAG, "Document not found.")
                    callback(false)
                }
            }
    }

    /**
     * Create New Document
     * @description This method creates a new document entry in the Firestore database.
     */
    fun createNewDocument(callback: (success: Boolean, documentId: String?) -> Unit) {
        val dataMapping = this.buildHashMap()
        this.fireStoreDB.collection(this.fireStoreCollection)
            .add(dataMapping)
            .addOnCompleteListener { documentReference ->
                if(documentReference.isSuccessful) {
                    Log.d(ContentValues.TAG, "Document added.")
                    callback(true, documentReference.result?.id)
                } else {
                    Log.d(ContentValues.TAG, "Document add failed.")
                    callback(false, null)
                }
            }
    }

    fun deleteDocument() {
        // TODO
    }

    fun updateDocument() {
        // TODO
    }
}