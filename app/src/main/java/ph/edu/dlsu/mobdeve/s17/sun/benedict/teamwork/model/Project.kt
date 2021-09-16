package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
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
class Project(): Serializable, Parcelable {

    // Instance Attributes
    @DocumentId
    lateinit var projectId: String
    lateinit var name: String
    lateinit var coverImage: String
    lateinit var description: String
    lateinit var about: String
    lateinit var completionDate: Date

    constructor(parcel: Parcel): this() {
        name = parcel.readString().toString()
        projectId = parcel.readString().toString()
        coverImage = parcel.readString().toString()
        description = parcel.readString().toString()
        about = parcel.readString().toString()
        completionDate = parcel.readSerializable() as Date
    }

    /**
     * This constructor is used when generating project instances from the Project Data Access Object.
     * @param projectId The project's document ID.
     * @param name The project's name.
     * @param description The project description.
     * @param about About the project.
     * @param coverImageUri A URI pointing to the project cover image. This image should be hosted in a CDN.
     * @param completionDate A Java Date Object timestamp that represents the project's completion date.
     */
    constructor(
        projectId: String, name: String, description: String,
        about: String, coverImageUri: String, completionDate: Date,) : this() {
        this.projectId = projectId
        this.name = name
        this.description = description
        this.about = about
        this.coverImage = coverImageUri
        this.completionDate = completionDate
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(projectId)
        parcel.writeString(coverImage)
        parcel.writeString(description)
        parcel.writeString(about)
        parcel.writeSerializable(completionDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Project> {
        override fun createFromParcel(parcel: Parcel): Project {
            return Project(parcel)
        }

        override fun newArray(size: Int): Array<Project?> {
            return arrayOfNulls(size)
        }
    }
}