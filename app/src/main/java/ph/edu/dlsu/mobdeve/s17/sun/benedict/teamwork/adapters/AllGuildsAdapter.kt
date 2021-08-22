package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Guild
import java.util.*

/**
 * Used to populate the list of guilds in SearchGuilds fragment
 */
class AllGuildsAdapter(private val guilds: ArrayList<Guild>) : RecyclerView.Adapter<AllGuildsAdapter.AllGuildsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AllGuildsViewHolder {
        // Inflate view holder
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.guilds_list_item, parent, false)
        val allGuildsViewHolder : AllGuildsViewHolder = AllGuildsViewHolder(view)

        return allGuildsViewHolder
    }

    override fun onBindViewHolder(holder: AllGuildsViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = guilds.size

    class AllGuildsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val siv_guild_dp : ShapeableImageView
        val tv_guild_name : TextView
        val tv_member_count : TextView
        val tv_guild_description : TextView
        val mbtn_action : MaterialButton

        init {
            siv_guild_dp = view.findViewById(R.id.siv_guild_dp)
            tv_guild_name = view.findViewById(R.id.tv_guild_name)
            tv_member_count = view.findViewById(R.id.tv_member_count)
            tv_guild_description = view.findViewById(R.id.tv_guild_description)
            mbtn_action = view.findViewById(R.id.mbtn_action)
            // Define click listener for mbtn_action
        }
    }
}