package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters

import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageHelper
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Comment
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.TimestampParser
import java.util.ArrayList

/**
 * Used to populate the list of comments in Guild comments activity
 */
class GuildCommentsAdapter(private var comments: ArrayList<Comment>, private val context: Context, private val guild: Guild) : RecyclerView.Adapter<GuildCommentsAdapter.GuildCommentsViewHolder>() {
    private val TAG = "GuildCommentsAdapter"
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GuildCommentsViewHolder {
        // Inflate view holder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_guild_comment, parent, false)
        return GuildCommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuildCommentsViewHolder, position: Int) {
        val comment: Comment = comments[position]
        holder.tv_username.text = comment.author.username
        holder.tv_comment.text= comment.comment
        Glide.with(context)
            .load(comment.author.profileImage)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.placeholder_guild_dp)
            .into(holder.siv_user_dp)
        // TODO: Implement date parser
        holder.tv_date_commented.text = "Commented on: ${TimestampParser(comment.date_commented).getDate()}"
    }

    override fun getItemCount() = comments.size

    class GuildCommentsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_username : TextView
        var tv_date_commented : TextView
        var siv_user_dp : ShapeableImageView
        var tv_comment : TextView

        init {
            tv_username = view.findViewById(R.id.tv_username)
            tv_date_commented = view.findViewById(R.id.tv_date_commented)
            siv_user_dp = view.findViewById(R.id.siv_user_dp)
            tv_comment = view.findViewById(R.id.tv_comment)
        }
    }

    fun setData(comments : ArrayList<Comment>) {
        this.comments.clear()
        this.comments.addAll(comments)
        // this.comments = comments
        notifyDataSetChanged()
    }
}