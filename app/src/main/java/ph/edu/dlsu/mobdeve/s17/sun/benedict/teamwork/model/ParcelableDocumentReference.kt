package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ParcelableDocumentReference : Parcelable {

    var collectionName: String = ""
    var documentId: String = ""

    constructor()

    constructor(collectionName: String, documentId: String) {
        this.collectionName = collectionName
        this.documentId = documentId
    }

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(), parcel.readString().toString()
    )

    constructor(documentReference: DocumentReference): this(documentReference.path, documentReference.id)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(this.collectionName)
        parcel.writeString(this.documentId)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun toDocumentReference(): DocumentReference {
        return FirebaseFirestore.getInstance().collection(collectionName).document(documentId)
    }

    companion object CREATOR : Parcelable.Creator<ParcelableDocumentReference> {
        override fun createFromParcel(parcel: Parcel): ParcelableDocumentReference {
            return ParcelableDocumentReference(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableDocumentReference?> {
            return arrayOfNulls(size)
        }
    }
}