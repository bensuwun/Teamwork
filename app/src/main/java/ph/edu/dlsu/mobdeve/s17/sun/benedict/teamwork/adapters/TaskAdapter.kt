package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task

class TaskAdapter(val tasks: ArrayList<Task>, val context: Context): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        // Inflate the ViewHolder
        return TaskViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_task, parent, false))
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, pos: Int) {
        val task = this.tasks[pos]
        // Do not change coverImage for now -- placeholders
        //holder.ivCoverImage =

        holder.tvTaskAbout.setText(task.about)
        holder.tvTaskName.setText(task.name)

        // Initialize onClickListener
        holder.ltTaskCard.setOnClickListener {
            // Bundle up data to send
            val bundle = bundleOf(
                "taskName" to task.name,
                "aboutTask" to task.about,
                "description" to task.description
            )
            holder.ltTaskCard.findNavController().navigate(R.id.navigateToTaskView, bundle)
        }
    }

    class TaskViewHolder(v : View): RecyclerView.ViewHolder(v) {
        // Initialize Views
        var ivCoverImage: ImageView = v.findViewById(R.id.task_cover_image_small)
        var tvTaskName: TextView = v.findViewById(R.id.task_item_name)
        var tvTaskAbout: TextView = v.findViewById(R.id.task_item_about)
        var ltTaskCard: ConstraintLayout = v.findViewById(R.id.task_list_item_container)
    }

}