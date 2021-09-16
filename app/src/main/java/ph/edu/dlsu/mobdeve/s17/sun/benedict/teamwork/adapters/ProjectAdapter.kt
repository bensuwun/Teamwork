package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.storage.FirebaseStorage
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Project

class ProjectAdapter(val projects: ArrayList<Project>, val context: Context): RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    val firebaseStorage = FirebaseStorage.getInstance()

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

        // Image processing
        val dlUrl = project.coverImage

        if(!dlUrl.isEmpty()) {
            val storageReference = this.firebaseStorage.getReference(dlUrl)
            storageReference.getBytes(1024*1024*15)
                .addOnSuccessListener {
                    val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                    holder.ivImage.setImageBitmap(
                        Bitmap.createScaledBitmap(
                            bmp,
                            holder.ivImage.width,
                            holder.ivImage.height,
                            false
                        )
                    )
                }
                .addOnFailureListener {
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }

        holder.mcvProject.setOnClickListener{
            val bundle = bundleOf(
                "project" to project as Parcelable
            )
            it.findNavController().navigate(R.id.navigateToViewProject, bundle)
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