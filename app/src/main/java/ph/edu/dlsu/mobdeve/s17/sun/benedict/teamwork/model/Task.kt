package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

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
data class Task(var name: String) : Serializable {

    // Instance Attributes
    var taskId: String = ""
    var isFinished: Boolean = false
    var coverImage: String = ""
    var description: String = ""
    var about: String = ""
    var dueDate: Date? = null
    var subtasks: ArrayList<DocumentReference> = ArrayList<DocumentReference>()
    var tags: String = ""

    /**
     * This constructor is used when generating project instances from the Project Data Access Object.
     * @param taskId The task's document ID.
     * @param name The task's name.
     * @param description The task description.
     * @param about About the task.
     * @param coverImageUri A URI pointing to the task cover image. This image should be hosted in a CDN.
     * @param dueDate A Java Date Object timestamp that represents the task's completion date.
     * @param subtasks The ArrayList of subtasks that belong to this parent task.
     */
    constructor(
        taskId: String, name: String, description: String,
        about: String, coverImageUri: String, dueDate: Date,
        subtasks: ArrayList<DocumentReference>): this(name) {

        this.taskId = taskId
        this.name = name
        this.description = description
        this.about = about
        this.coverImage = coverImageUri
        this.dueDate = dueDate
        this.subtasks = subtasks
    }

}