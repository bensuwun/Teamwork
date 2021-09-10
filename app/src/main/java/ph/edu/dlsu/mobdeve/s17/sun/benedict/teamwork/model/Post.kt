package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import java.util.*
import kotlin.collections.ArrayList

class Post() : Parcelable{
    @DocumentId
    var docId : String = ""
    var author : String = ""
    var title : String = ""
    var description : String = ""
    var tags : Tags = Tags()
    var likes : Long = 0
    var comments : Long = 0
    var date_posted : Timestamp = Timestamp(Date())

    constructor(
        author: String,
        title: String,
        description: String,
        likes: Long,
        comments: Long,
        date_posted: Timestamp
    ) : this() {
        this.author = author
        this.title = title
        this.description = description
        this.likes = likes
        this.comments = comments
        this.date_posted = date_posted
    }

    constructor(parcel: Parcel) : this() {
        docId = parcel.readString().toString()
        author = parcel.readString().toString()
        title = parcel.readString().toString()
        description = parcel.readString().toString()
        tags = parcel.readParcelable(Tags::class.java.classLoader)!!
        likes = parcel.readLong()
        comments = parcel.readLong()
        date_posted = parcel.readParcelable(Timestamp::class.java.classLoader)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(docId)
        parcel.writeString(author)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeParcelable(tags, flags)
        parcel.writeLong(likes)
        parcel.writeLong(comments)
        parcel.writeParcelable(date_posted, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }


}