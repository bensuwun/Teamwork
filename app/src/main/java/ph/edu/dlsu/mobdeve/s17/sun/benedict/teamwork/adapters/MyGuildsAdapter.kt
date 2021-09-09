package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.guilds.MyGuildsDirections
import java.util.*

/**
 * Used to populate the list of guilds the user has
 */
class MyGuildsAdapter(private var guilds: ArrayList<Guild>, private val context: Context) : RecyclerView.Adapter<MyGuildsAdapter.MyGuildsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyGuildsViewHolder {
        // Inflate view holder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.guilds_list_item, parent, false)
        return MyGuildsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyGuildsViewHolder, position: Int) {
        val guild : Guild = guilds[position]
        holder.tv_guild_name.text = guild.name
        holder.tv_guild_description.text = guild.description
        holder.tv_member_count.text = "Members: ${String.format("%,d", guild.member_count)}"
        // Use Picasso/Glide here --> String (Download URL) -> Int (Resource ID)
        holder.siv_guild_dp.setImageResource(R.drawable.my_guilds_empty)
        // Define click listener for mbtn_action
        holder.mbtn_action.setOnClickListener {
            // Go to guild's view dashboard
            val bundle = bundleOf(
                "guildId" to guild.guildId,
                "name" to guild.name,
                "member_count" to guild.member_count,
                "description" to guild.description
            )
            holder.mbtn_action.findNavController().navigate(R.id.navigateToGuildDashboard, bundle)
        }
    }

    override fun getItemCount() = guilds.size

    class MyGuildsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var siv_guild_dp : ShapeableImageView
        var tv_guild_name : TextView
        var tv_member_count : TextView
        var tv_guild_description : TextView
        var mbtn_action : MaterialButton

        init {
            siv_guild_dp = view.findViewById(R.id.siv_guild_dp)
            tv_guild_name = view.findViewById(R.id.tv_guild_name)
            tv_member_count = view.findViewById(R.id.tv_member_count)
            tv_guild_description = view.findViewById(R.id.tv_guild_description)
            mbtn_action = view.findViewById(R.id.mbtn_action)
        }
    }

    fun setData(guilds : ArrayList<Guild>) {
        this.guilds = guilds
        notifyDataSetChanged()
    }
}