package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable
import java.util.*

/**
 * This is the project data class, where an instance of Project is stored.
 * @author Adriel Isaiah V. Amoguis
 *
 * @constructor The primary constructor accepts the name of the project as the only parameter.
 * The project name is the only required attribute to create a project. A more specified constructor
 * is declared within the body that will handle instance generation from the data access object from Firestore.
 *
 * @param name The project name.
 */
data class Project(var name: String) : Serializable {

    // Instance Attributes
    var projectId: String = ""
    var coverImage: String = ""
    var description: String = ""
    var about: String = ""
    var completionDate: Date? = null
    var tasks: ArrayList<DocumentReference> = ArrayList<DocumentReference>()

    /**
     * This constructor is used when generating project instances from the Project Data Access Object.
     * @param projectId The project's document ID.
     * @param name The project's name.
     * @param description The project description.
     * @param about About the project.
     * @param coverImageUri A URI pointing to the project cover image. This image should be hosted in a CDN.
     * @param completionDate A Java Date Object timestamp that represents the project's completion date.
     * @param tasks The ArrayList of tasks that belong to this project.
     */
    constructor(
        projectId: String, name: String, description: String,
        about: String, coverImageUri: String, completionDate: Date,
        tasks: ArrayList<DocumentReference>): this(name) {

        this.projectId = projectId
        this.name = name
        this.description = description
        this.about = about
        this.coverImage = coverImageUri
        this.completionDate = completionDate
        this.tasks = tasks
    }

    companion object {
        fun initSampleData() : ArrayList<Project>{
            var projects : ArrayList<Project> = ArrayList()
            for (i in 0..2){
                projects.add(Project(
                    "1",
                    "MOBDEVE MCO2",
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor.",
                    "Android Development with Kotlin",
                    "Test",
                    Date(),
                    ArrayList()
                ))
            }

            return projects
        }
    }

}