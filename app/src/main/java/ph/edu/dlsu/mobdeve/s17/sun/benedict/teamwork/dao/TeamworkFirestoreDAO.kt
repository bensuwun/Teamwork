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
 * This class abstracts most of the client-db communication. To use it, simply
 * extend it to your own DAO, then implement the needed abstract functions.
 *
 * @author Adriel Isaiah V. Amoguis
 */
abstract class TeamworkFirestoreDAO() {

    // TAG
    private val TAG = "TeamworkFirestoreDAO"

    // Firestore
    protected val fireStoreDB = FirebaseFirestore.getInstance()

    /**
     * The collection name that will be used to query the Firestore database.
     */
    abstract val fireStoreCollection: String

    // Query Results
    /**
     * An ArrayList where results will be stored if the query returns multiple documents.
     */
    abstract var queryResults: ArrayList<Any>

    /**
     * The instance of the object that this DAO represents. This DAO can be replaced with
     * the object that will be used to update or delete a document in the Firestore database.
     * Furthermore, this is also where the result is stored after executing a query that returns
     * a single document from Firestore.
     */
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
     * @param documentId The Firestore documentId of the document as a String.
     * @param callback A callback function that takes in the `success` parameter as a boolean.
     */
    fun getDocumentById(documentId: String, callback: (success: Boolean) -> Unit) {
        fireStoreDB.collection(fireStoreCollection)
            .document(documentId)
            .get()
            .addOnCompleteListener { document ->
                val data = document.result
                if(data != null && document.isSuccessful) {
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
     * Get Document By Field Value
     * @description Searches the Firestore database for any documents that match the query parameters. Returns the results
     * to the queryResults ArrayList of this class.
     * @param fieldName A string-key of the field to test.
     * @param fieldValue The value to be matched.
     * @param callback A callback function that takes in the `success` parameter as a boolean.
     */
    fun getDocumentsByFieldValue(fieldName: String, fieldValue: Any, callback: (success: Boolean) -> Unit) {
        fireStoreDB.collection(fireStoreCollection)
            .whereEqualTo(fieldName, fieldValue)
            .get()
            .addOnCompleteListener { taskQuery ->
                if(taskQuery.isSuccessful) {
                    Log.d(
                        TAG,
                        "Got the document(s) using field name $fieldName with a value of $fieldValue"
                    )
                    taskQuery.result?.iterator()?.forEach {
                        this.queryResults.add(this.parseDocument(it))
                    }
                    callback(true)
                } else {
                    Log.e(TAG, "Unable to get document using field $fieldName with value $fieldValue.")
                    callback(false)
                }
            }
    }

    /**
     * Create New Document
     * @description This method creates a new document entry in the Firestore database.
     * @param callback A callback function that takes in the `success` parameter as a boolean.
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
                    Log.e(ContentValues.TAG, "Document add failed.")
                    callback(false, null)
                }
            }
    }

    /**
     * Delete Document
     * @param documentId The Firestore documentId of the document as a String.
     * @param callback A callback function that takes in the `success` parameter as a boolean.
     * @description This method deletes a document given its documentId.
     */
    fun deleteDocument(documentId: String, callback: (success: Boolean) -> Unit) {
        fireStoreDB.collection(fireStoreCollection)
            .document(documentId)
            .delete()
            .addOnCompleteListener { taskStatus ->
                if(taskStatus.isSuccessful) {
                    Log.d(ContentValues.TAG, "Document Deleted.")
                    callback(true)
                } else {
                    Log.e(ContentValues.TAG, "Document Delete Failed")
                    callback(false)
                }
            }
    }

    /**
     * Update Document
     * @param documentId The Firestore documentId of the document as a String.
     * @param callback A callback function that takes in the `success` parameter as a boolean.
     * @description This method updates the document on Firestore based on the Document object this class has.
     */
    fun updateDocument(documentId: String, callback: (success: Boolean) -> Unit) {
        val dataMapping = this.buildHashMap()
        fireStoreDB.collection(fireStoreCollection)
            .document(documentId)
            .update(dataMapping)
            .addOnCompleteListener { taskStatus ->
                if(taskStatus.isSuccessful) {
                    Log.d(ContentValues.TAG, "Document Updated.")
                    callback(true)
                } else {
                    Log.e(ContentValues.TAG, "Document Update Failed.")
                    callback(false)
                }
            }
    }
}