package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters

import android.content.Context
import android.os.Parcel
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
import java.util.*
import kotlin.collections.ArrayList

class TaskAdapter(val tasks: ArrayList<Task>, val context: Context): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    var parentTask: Task? = null

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
        holder.tvTaskDate.setText(task.dueDate.toString().subSequence(0, 10))
        holder.tvTaskTime.setText(task.dueDate.toString().subSequence(11, 19))

        // Check if the task is overdue
        if(task.dueDate.before(Date())) {
            // Set colors to red
            holder.tvTaskDate.setTextColor(context.resources.getColor(R.color.warning_red))
            holder.tvTaskTime.setTextColor(context.resources.getColor(R.color.warning_red))
        }

        // Initialize onClickListener
        holder.ltTaskCard.setOnClickListener {
            // Bundle up data to send
            val bundle = bundleOf(
                "taskObject" to task,
                "parentTask" to parentTask
            )
            holder.ltTaskCard.findNavController().navigate(R.id.navigateToTaskView, bundle)
        }
    }

    class TaskViewHolder(v : View): RecyclerView.ViewHolder(v) {
        // Initialize Views
        var tvTaskTime: TextView = v.findViewById(R.id.task_item_due_time)
        var tvTaskDate: TextView = v.findViewById(R.id.task_item_due_date)
        var tvTaskName: TextView = v.findViewById(R.id.task_item_name)
        var tvTaskAbout: TextView = v.findViewById(R.id.task_item_about)
        var ltTaskCard: ConstraintLayout = v.findViewById(R.id.task_list_item_container)
    }

}