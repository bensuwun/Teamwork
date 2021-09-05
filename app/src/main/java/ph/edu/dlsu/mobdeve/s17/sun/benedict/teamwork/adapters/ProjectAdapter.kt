package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Project

class ProjectAdapter(val projects: ArrayList<Project>, val context: Context): RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        // Inflate the ViewHolder
        return ProjectViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_project, parent, false))
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, pos: Int) {
        val project = this.projects[pos]
        // Do not change coverImage for now -- placeholders
        //holder.ivCoverImage =
        holder.tvName.text = project.name
        holder.tvAbout.text = project.about
        holder.tvDesc.text = project.description
        holder.mcvProject.setOnClickListener{
            // TODO: Pass necessary data here
            it.findNavController().navigate(R.id.navigateToViewProject)
        }

    }

    class ProjectViewHolder(v : View): RecyclerView.ViewHolder(v) {
        // Initialize Views
        var tvName: TextView = v.findViewById(R.id.tv_project_name)
        var tvDesc: TextView = v.findViewById(R.id.tv_project_desc)
        var ivImage: ImageView = v.findViewById(R.id.iv_project_image)
        var tvAbout: TextView = v.findViewById(R.id.tv_project_about)
        var mcvProject : MaterialCardView = v.findViewById(R.id.mcv_project)
    }

}