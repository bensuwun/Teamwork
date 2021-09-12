package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils

import android.text.format.DateUtils
import android.util.Log
import com.google.firebase.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class TimestampParser(val timestamp : Timestamp) {
    fun getTimeAgo(){
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val date = timestamp.toDate()
        Log.d("TimeStampParser", "${DateFormat.getDateInstance().format(timestamp.toDate().time)}")


        //val formattedDate = inputFormat.parse(date)
        //val timeAgo = DateUtils.getRelativeTimeSpanString(formattedDate.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS) as String

        //return timeAgo
    }
    fun getDate() : String{
        return DateFormat.getDateInstance().format(timestamp.toDate().time)
    }
}