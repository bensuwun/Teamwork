package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

/**
 * This is the User data class, where an instance of a user is stored.
 * @author Adriel Isaiah V. Amoguis
 *
 * @constructor The primary constructor is a generic constructor that accepts only
 * the username, since these two are the most basic requirements
 * of creating a user account. For new users, a password is set later on using the
 * set password method. A more specified constructor is declared within the body that
 * will handle instance generation from the data access object from Firestore.
 *
 * @param username - The username of the user.
 * @param emailAddress - The user's email address.
 */
class User() : Serializable, Parcelable{

    // Instance Attributes (not including default constructor attributes)
    // Getters and setters defined here by Kotlin's get/set features
    @DocumentId
    var authUid: String = ""
    var username: String = ""
    var profileImage: String = ""

    constructor(parcel: Parcel) : this() {
        authUid = parcel.readString().toString()
        username = parcel.readString().toString()
        profileImage = parcel.readString().toString()
    }

    /**
     * This constructor is used when generating user instances from the User Data Access Object.
     * @param username - The user's username.
     * @param authUid - The document ID for this User.
     * @param profileImageUri - The URL for the image of the user's account. This should be from a CDN.
     */
    constructor(authUid: String, username: String, profileImageUri: String): this() {
        this.authUid = authUid
        this.profileImage = profileImageUri
    }

    override fun toString(): String {
        val sb = StringBuilder("[User] ")
        sb.append(this.authUid)
        sb.append(" | ")
        sb.append(this.username)
        return sb.toString()
    }

    /**
     * Parcelable implementations.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(authUid)
        parcel.writeString(username)
        parcel.writeString(profileImage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}