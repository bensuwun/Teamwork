package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import java.util.*
import kotlin.collections.ArrayList

class Comment() : Parcelable{
    @DocumentId
    var docId : String = ""
    var author : User = User()
    var comment : String = ""
    var date_commented : Timestamp = Timestamp(Date())

    constructor(
        author: User,
        comment: String,
        date_commented : Timestamp
    ) : this() {
        this.author = author
        this.comment = comment
        this.date_commented = date_commented
    }

    constructor(parcel: Parcel) : this() {
        docId = parcel.readString().toString()
        author = parcel.readParcelable(User::class.java.classLoader)!!
        comment = parcel.readString().toString()
        date_commented = parcel.readParcelable(Timestamp::class.java.classLoader)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(docId)
        parcel.writeParcelable(author, flags)
        parcel.writeString(comment)
        parcel.writeParcelable(date_commented, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }


}