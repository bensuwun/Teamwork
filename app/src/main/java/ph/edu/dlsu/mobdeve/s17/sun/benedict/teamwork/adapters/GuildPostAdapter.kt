package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageHelper
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Post
import java.util.ArrayList

/**
 * Used to populate the list of posts in Guild Posts activity
 */
class GuildPostAdapter(private var posts: ArrayList<Post>, private val context: Context) : RecyclerView.Adapter<GuildPostAdapter.GuildPostViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GuildPostViewHolder {
        // Inflate view holder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_guild_post, parent, false)
        return GuildPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuildPostViewHolder, position: Int) {
        holder.tv_username.text = posts[position].author.username
        holder.tv_post_description.text = posts[position].description
        holder.tv_post_title.text = posts[position].title
        holder.iv_like_icon.setOnClickListener {
            /*
            // if is liked
            holder.iv_like_icon.setImageResource(R.drawable.ic_baseline_favorite_24)

            // else
            holder.iv_like_icon.setImageResource(R.drawable.ic_outline_favorite_border_24)
            */
        }


    }

    override fun getItemCount() = posts.size

    class GuildPostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cl_post : ConstraintLayout      // Main Container
        var tv_username : TextView
        var tv_date_posted : TextView
        var siv_user_dp : ShapeableImageView
        var cg_tags : ChipGroup
        var tv_post_description: TextView
        var tv_post_title: TextView
        var iv_like_icon: AppCompatImageView
        var tv_likes: TextView
        var tv_comments: TextView

        init {
            cl_post = view.findViewById(R.id.cl_post)
            tv_username = view.findViewById(R.id.tv_username)
            tv_date_posted = view.findViewById(R.id.tv_date_posted)
            siv_user_dp = view.findViewById(R.id.siv_user_dp)
            cg_tags = view.findViewById(R.id.cg_tags)
            tv_post_description = view.findViewById(R.id.tv_post_description)
            tv_post_title = view.findViewById(R.id.tv_post_title)
            iv_like_icon = view.findViewById(R.id.iv_like_icon)
            tv_likes = view.findViewById(R.id.tv_likes)
            tv_comments = view.findViewById(R.id.tv_comments)
        }
    }
}