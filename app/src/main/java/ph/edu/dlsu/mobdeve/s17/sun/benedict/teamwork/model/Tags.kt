package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

import android.os.Parcel
import android.os.Parcelable

class Tags() : Parcelable {
    var support : Boolean = false
    var social : Boolean = false
    var challenge : Boolean = false

    constructor(parcel: Parcel) : this() {
        support = parcel.readByte() != 0.toByte()
        social = parcel.readByte() != 0.toByte()
        challenge = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (support) 1 else 0)
        parcel.writeByte(if (social) 1 else 0)
        parcel.writeByte(if (challenge) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Tags> {
        override fun createFromParcel(parcel: Parcel): Tags {
            return Tags(parcel)
        }

        override fun newArray(size: Int): Array<Tags?> {
            return arrayOfNulls(size)
        }
    }
}