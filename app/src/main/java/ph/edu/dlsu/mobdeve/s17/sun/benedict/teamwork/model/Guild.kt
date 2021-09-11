package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId

class Guild() : Parcelable{
    // Reference: https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/DocumentId
    @DocumentId
    var name : String = ""
    var master : User = User()
    var profileImage : String = ""
    var headerImage : String = ""
    var description : String = ""
    var memberCount : Long = 0

    /**
     * Constructor used prior to image uploads.
     */
    constructor(name: String, master: User, description: String, memberCount: Long) : this() {
        this.name = name
        this.master = master
        this.description = description
        this.memberCount = memberCount
    }

    /**
     * No guild DP, for testing.
     * TODO: Remove when done
     */
    constructor(guildId : String, name: String, description: String, memberCount: Long) : this() {
        this.name = name
        this.description = description
        this.memberCount = memberCount
    }

    /**
     * Parcelable implementations
     */
    constructor(parcel: Parcel) : this() {
        name = parcel.readString().toString()
        master = parcel.readParcelable(User::class.java.classLoader)!!
        profileImage = parcel.readString().toString()
        headerImage = parcel.readString().toString()
        description = parcel.readString().toString()
        memberCount = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeParcelable(master, flags)
        parcel.writeString(profileImage)
        parcel.writeString(headerImage)
        parcel.writeString(description)
        parcel.writeLong(memberCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Guild> {
        override fun createFromParcel(parcel: Parcel): Guild {
            return Guild(parcel)
        }

        override fun newArray(size: Int): Array<Guild?> {
            return arrayOfNulls(size)
        }
    }
}