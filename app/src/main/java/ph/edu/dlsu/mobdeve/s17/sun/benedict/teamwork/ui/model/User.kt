package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.model

import java.io.Serializable;

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

    /**
     * This constructor is used when generating user instances from the User Data Access Object.
     * @param userId - The user's ID generated from Firestore's DocumentId.
     * @param username - The user's username.
     * @param emailAddress - The user's email address.
     */
    constructor(userId: String, username: String, emailAddress: String): this(username, emailAddress)

    // Instance attributes


}