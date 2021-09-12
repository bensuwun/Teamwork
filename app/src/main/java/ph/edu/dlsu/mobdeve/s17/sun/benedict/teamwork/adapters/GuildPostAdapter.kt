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
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Post
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.TimestampParser
import java.util.ArrayList

/**
 * Used to populate the list of posts in Guild Posts activity
 */
class GuildPostAdapter(private var posts: ArrayList<Post>, private val context: Context, private val guild: Guild) : RecyclerView.Adapter<GuildPostAdapter.GuildPostViewHolder>() {
    private val TAG = "GuildPostAdapter"
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GuildPostViewHolder {
        // Inflate view holder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_guild_post, parent, false)
        return GuildPostViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuildPostViewHolder, position: Int) {
        val post : Post = posts[position]
        holder.tv_username.text = post.author.username
        Glide.with(context)
            .load(post.author.profileImage)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.placeholder_guild_dp)
            .into(holder.siv_user_dp)
        // TODO: Implement date parser
        holder.tv_date_posted.text = "Posted on: ${TimestampParser(post.date_posted).getDate()}"
        holder.tv_post_description.text = post.description
        holder.tv_post_title.text = post.title
        holder.tv_comments.text = post.comments.toString()
        holder.tv_likes.text = post.likes.toString()
        holder.iv_like_icon.setOnClickListener {
            // TODO: Implement like functionality
            /*
            // if is liked
            holder.iv_like_icon.setImageResource(R.drawable.ic_baseline_favorite_24)

            // else
            holder.iv_like_icon.setImageResource(R.drawable.ic_outline_favorite_border_24)
            */
        }
        holder.post_container.setOnClickListener(){
            // View post
            val bundle = Bundle()
            bundle.putParcelable("guild", guild)
            bundle.putParcelable("post", post)

            holder.post_container.findNavController().navigate(R.id.navigateToViewPost, bundle)
        }
        // Chips
        if(post.tags.challenge || post.tags.support || post.tags.social){
            if(!post.tags.challenge) holder.chipChallenge.visibility = View.GONE
            if(!post.tags.support) holder.chipSupport.visibility = View.GONE
            if(!post.tags.social) holder.chipSocial.visibility = View.GONE
        }
        else{
            holder.chipGroup.visibility = View.GONE
        }
    }

    override fun getItemCount() = posts.size

    class GuildPostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var post_container : LinearLayoutCompat      // Main Container
        var tv_username : TextView
        var tv_date_posted : TextView
        var siv_user_dp : ShapeableImageView
        var cg_tags : ChipGroup
        var tv_post_description: TextView
        var tv_post_title: TextView
        var iv_like_icon: AppCompatImageView
        var tv_likes: TextView
        var tv_comments: TextView
        var chipGroup : ChipGroup
        var chipSupport : Chip
        var chipSocial : Chip
        var chipChallenge : Chip

        init {
            post_container = view.findViewById(R.id.post_container)
            tv_username = view.findViewById(R.id.tv_username)
            tv_date_posted = view.findViewById(R.id.tv_date_posted)
            siv_user_dp = view.findViewById(R.id.siv_user_dp)
            cg_tags = view.findViewById(R.id.cg_tags)
            tv_post_description = view.findViewById(R.id.tv_post_description)
            tv_post_title = view.findViewById(R.id.tv_post_title)
            iv_like_icon = view.findViewById(R.id.iv_like_icon)
            tv_likes = view.findViewById(R.id.tv_likes)
            tv_comments = view.findViewById(R.id.tv_comments)
            chipGroup = view.findViewById(R.id.cg_tags)
            chipSupport = view.findViewById(R.id.chip_support)
            chipSocial = view.findViewById(R.id.chip_social)
            chipChallenge = view.findViewById(R.id.chip_challenge)
        }
    }

    fun setData(posts : ArrayList<Post>) {
        this.posts = posts
        notifyDataSetChanged()
    }
}