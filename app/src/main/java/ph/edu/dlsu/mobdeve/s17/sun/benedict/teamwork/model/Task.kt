package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable
import java.util.*

/**
 * This is the task data class, where an instance of Task is stored.
 * @author Adriel Isaiah V. Amoguis
 *
 * @constructor The primary constructor accepts the name of the task as the only parameter.
 * The task name is the only required attribute to create a task. A more specified constructor
 * is declared within the body that will handle instance generation from the data access object from Firestore.
 *
 * @param name The task name.
 */
class Task(var name: String) : Serializable, Parcelable {

    // Instance Attributes
    @DocumentId
    lateinit var taskId: String
    var isFinished: Boolean = false
    var description: String = ""
    var about: String = ""
    var dueDate: Date? = null
    var subtasks: ArrayList<ParcelableDocumentReference> = ArrayList<ParcelableDocumentReference>()
    lateinit var tags: ArrayList<String>

    constructor(parcel: Parcel) : this(parcel.readString().toString()) {
        this.taskId = parcel.readString().toString()
        this.about = parcel.readString().toString()
        this.description = parcel.readString().toString()
        this.dueDate = parcel.readSerializable() as Date
        this.subtasks = parcel.readArrayList(ParcelableDocumentReference::class.java.classLoader) as ArrayList<ParcelableDocumentReference>
        parcel.readStringList(this.tags)
    }

    /**
     * This constructor is used when generating project instances from the Project Data Access Object.
     * @param taskId The task's document ID.
     * @param name The task's name.
     * @param description The task description.
     * @param about About the task.
     * @param dueDate A Java Date Object timestamp that represents the task's completion date.
     * @param subtasks The ArrayList of subtasks that belong to this parent task.
     */
    constructor(
        taskId: String, name: String, description: String,
        about: String, dueDate: Date,
        subtasks: ArrayList<ParcelableDocumentReference>, tags: ArrayList<String>): this(name) {
        this.taskId = taskId
        this.name = name
        this.description = description
        this.about = about
        this.dueDate = dueDate
        this.subtasks = subtasks
        this.tags = tags
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this.name)
        parcel.writeString(this.taskId)
        parcel.writeString(this.about)
        parcel.writeString(this.description)
        parcel.writeSerializable(this.dueDate)
        parcel.writeTypedList(this.subtasks)
        parcel.writeStringList(this.tags)
    }

    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(parcel: Parcel): Task {
            return Task(parcel)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }

}