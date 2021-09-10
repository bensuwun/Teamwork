package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId

class Guild() : Parcelable{
    // Reference: https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/DocumentId
    @DocumentId
    var guildId : String = ""
    var name : String = ""
    var description : String = ""
    lateinit var guild_dp : String
    var member_count : Long = -1

    constructor(parcel: Parcel) : this() {
        guildId = parcel.readString().toString()
        name = parcel.readString().toString()
        description = parcel.readString().toString()
        guild_dp = parcel.readString().toString()
        member_count = parcel.readLong()
    }

    constructor(guildID: String, name : String, description : String, guild_dp : String, member_count : Long) : this() {
        this.name = name
        this.description = description
        this.guild_dp = guild_dp
        this.member_count = member_count
    }

    /**
     * No guild DP, for testing.
     * TODO: Remove when done
     */
    constructor(guildId : String, name: String, description: String, member_count: Long) : this() {
        this.name = name
        this.description = description
        this.member_count = member_count
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(guildId)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(guild_dp)
        parcel.writeLong(member_count)
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