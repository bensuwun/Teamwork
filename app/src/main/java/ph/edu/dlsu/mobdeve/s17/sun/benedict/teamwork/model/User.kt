package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

/**
 * This is the User data class, where an instance of a user is stored.
 * @author Adriel Isaiah V. Amoguis
 *
 * @constructor The primary constructor is a generic constructor that accepts only
 * the username and email address, since these two are the most basic requirements
 * of creating a user account. For new users, a password is set later on using the
 * set password method. A more specified constructor is declared within the body that
 * will handle instance generation from the data access object from Firestore.
 *
 * @param username - The username of the user.
 * @param emailAddress - The user's email address.
 */
data class User(val username: String, val emailAddress: String) : Serializable {

    // Instance Attributes (not including default constructor attributes)
    // Getters and setters defined here by Kotlin's get/set features
    var userId: String = ""
    var profileImageUri: String = ""
    var projectReferences: ArrayList<DocumentReference> = ArrayList()
    var taskReferences: ArrayList<DocumentReference> = ArrayList()

    // The value of this instance attribute cannot be extracted.
    var password: String = ""

    /**
     * This constructor is used when generating user instances from the User Data Access Object.
     * @param userId - The user's ID generated from Firestore's DocumentId.
     * @param username - The user's username.
     * @param emailAddress - The user's email address.
     * @param profileImageUri - The URL for the image of the user's account. This should be from a CDN.
     * @param projects - A Java ArrayList containing project instances.
     * @param tasks - A Java ArrayList containing task instances.
     */
    constructor(userId: String, username: String, emailAddress: String, profileImageUri: String,
                projects: ArrayList<DocumentReference>, tasks: ArrayList<DocumentReference>): this(username, emailAddress) {
        this.userId = userId
        this.profileImageUri = profileImageUri
        this.projectReferences = projects
        this.taskReferences = tasks
    }

    /**
     * This constructor is used when creating a new user.
     * @param username - The user's username when creating an account.
     * @param emailAddress - The user's email address.
     * @param password - The user's password.
     */
    constructor(username: String, emailAddress: String, password: String): this(username, emailAddress) {
        this.password = password
    }

    override fun toString(): String {
        val sb = StringBuilder("[User] ")
        sb.append(this.userId)
        sb.append(" | ")
        sb.append(this.username)
        sb.append(" | ")
        sb.append(this.emailAddress)
        return sb.toString()
    }

}