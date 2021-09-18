package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.ui.home.tasks

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.TasksScopes
import com.google.api.services.tasks.model.TaskList
import com.squareup.okhttp.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.adapters.TaskAdapter
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.TaskDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentViewTaskBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Project
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences
import java.io.IOException
import java.util.*

class SpecificTaskView: Fragment() {

    lateinit var fragmentBinding: FragmentViewTaskBinding
    private val TAG = "SpecificTaskView"

    lateinit var task: Task
    lateinit var userPreferences : UserPreferences

    var editMode: Boolean = false

    private val broadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            when(intent?.action) {
                "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_user_task" -> {
                    // Show deleted toast then pop backstack
                    Toast.makeText(requireContext(), "Task has been deleted.", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.fromTaskViewNavigateBackToTasksFragment)
                }
                "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_user_task_failed" -> {
                    // Show failed to delete message
                    Toast.makeText(requireContext(), "Failed to delete task.", Toast.LENGTH_LONG).show()
                }
                "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.update_user_task" -> {
                    Toast.makeText(requireContext(), "Successfully updated the task.", Toast.LENGTH_LONG).show()
                }
                "ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.update_user_task_failed" -> {
                    Toast.makeText(requireContext(), "Failed to update task.", Toast.LENGTH_LONG).show()
                }
                TaskDAO.GET_TASK_SUBTASKS_SUCCESS_INTENT -> {
                    // Activate the subtask recycler view
                    fragmentBinding.subtaskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                    subtaskAdapter = TaskAdapter((intent.extras!!["subtaskList"] as Array<Task>).toCollection(ArrayList()), requireContext())
                    subtaskAdapter.parentTask = task
                    fragmentBinding.subtaskRecyclerView.adapter = subtaskAdapter

                    // Show the recycler view
                    if(subtaskAdapter.tasks.size > 0) {
                        fragmentBinding.emptySubtasksGraphic.visibility = View.GONE
                        fragmentBinding.subtaskRecyclerView.visibility = View.VISIBLE
                    }
                }
                TaskDAO.GET_TASK_SUBTASKS_FAILURE_INTENT -> {
                    Toast.makeText(requireContext(), "Failed to query subtasks.", Toast.LENGTH_LONG).show()
                }
                TaskDAO.DELETE_SUBTASK_SUCCESS_INTENT -> {
                    Toast.makeText(requireContext(), "Deleted subtask.", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
                TaskDAO.DELETE_SUBTASK_FAILURE_INTENT -> {
                    Toast.makeText(requireContext(), "Failed to delete subtask.", Toast.LENGTH_LONG).show()
                }
                TaskDAO.UPDATE_SUBTASK_SUCCESS_INTENT -> {
                    Toast.makeText(requireContext(), "Subtask updated.", Toast.LENGTH_SHORT).show()
                }
                TaskDAO.UPDATE_SUBTASK_FAILURE_INTENT -> {
                    Toast.makeText(requireContext(), "Subtask update failed.", Toast.LENGTH_LONG).show()
                }
                TaskDAO.DELETE_PROJECT_TASK_SUCCESS_INTENT -> {
                    Toast.makeText(requireContext(), "Deleted project task.", Toast.LENGTH_LONG).show()
                    findNavController().popBackStack()
                }
                TaskDAO.DELETE_PROJECT_TASK_FAILURE_INTENT -> {
                    Toast.makeText(requireContext(), "Failed to delete project task.", Toast.LENGTH_LONG).show()
                }
                TaskDAO.UPDATE_PROJECT_TASK_SUCCESS_INTENT -> {
                    Toast.makeText(requireContext(), "Updated project task.", Toast.LENGTH_LONG).show()
                }
                TaskDAO.UPDATE_PROJECT_TASK_FAILURE_INTENT -> {
                    Toast.makeText(requireContext(), "Failed to update project task.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    lateinit var subtaskAdapter: TaskAdapter

    var yearSet: Int = 0
    var monthSet: Int = 0
    var daySet: Int = 0
    var hourSet: Int = 0
    var minuteSet: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize Options Menu
        setHasOptionsMenu(true)

        // Initialize the view binding
        fragmentBinding = FragmentViewTaskBinding.inflate(inflater, container, false)
        val view = fragmentBinding.root

        // Get the bundled data
        this.task = arguments?.getParcelable<Task>("taskObject")!!

        // Set userPreferences
        this.userPreferences = UserPreferences(requireContext())

        // Apply to views
        Log.d("SpecificTaskView:onCreateView", task.completed.toString())
        fragmentBinding.taskViewAbout.setText(task.about)
        fragmentBinding.taskViewDesc.setText(task.description)
        fragmentBinding.taskCheckboxIsDone.isChecked = task.completed
        fragmentBinding.viewTaskDueDateTime.setText("${this.task.dueDate.toString().subSequence(0, 19)}")
        (activity as AppCompatActivity).supportActionBar?.title = task.name

        // Check if there are subtasks
        if(!task.isSubtask) {
            val subtaskDAO = TaskDAO(requireContext())
            UserPreferences(requireContext()).getLoggedInUser()?.let {
                subtaskDAO.getSubtasks(it.authUid, task.taskId)
            }
        }

        if(task.isSubtask) {
            fragmentBinding.taskItemCard2.visibility = View.GONE
            fragmentBinding.fabNewSubtask.visibility = View.GONE

            //this.parentTask = arguments?.getParcelable<Task>("parentTask")!!
        }

        // Check if the task is overdue
        if(task.dueDate.before(Date()) && !task.completed) {
            // Set colors to red
            fragmentBinding.viewTaskDueDateTime.setTextColor(requireContext().resources.getColor(R.color.warning_red))
        }

        // Register the Broadcast Receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_user_task")
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_user_task_failed")
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.update_user_task")
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.update_user_task_failed")
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.timepicker_new_time_set")
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.datepicker_new_date_set")
        intentFilter.addAction(TaskDAO.GET_TASK_SUBTASKS_SUCCESS_INTENT)
        intentFilter.addAction(TaskDAO.GET_TASK_SUBTASKS_FAILURE_INTENT)
        intentFilter.addAction(TaskDAO.GET_TASK_SUBTASKS_SUCCESS_INTENT)
        intentFilter.addAction(TaskDAO.GET_TASK_SUBTASKS_FAILURE_INTENT)
        intentFilter.addAction(TaskDAO.DELETE_SUBTASK_SUCCESS_INTENT)
        intentFilter.addAction(TaskDAO.DELETE_SUBTASK_FAILURE_INTENT)
        intentFilter.addAction(TaskDAO.UPDATE_SUBTASK_SUCCESS_INTENT)
        intentFilter.addAction(TaskDAO.UPDATE_SUBTASK_FAILURE_INTENT)
        intentFilter.addAction(TaskDAO.DELETE_PROJECT_TASK_SUCCESS_INTENT)
        intentFilter.addAction(TaskDAO.DELETE_PROJECT_TASK_FAILURE_INTENT)
        intentFilter.addAction(TaskDAO.UPDATE_PROJECT_TASK_SUCCESS_INTENT)
        intentFilter.addAction(TaskDAO.UPDATE_PROJECT_TASK_FAILURE_INTENT)
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(broadcastReceiver, intentFilter)

        // Initialize OnClickListener for the FAB
        this.fragmentBinding.fabEditTask.setOnClickListener { v ->
            if(editMode) {
                // Set the icon back to the pencil
                // Commit changes
                fragmentBinding.taskViewAbout.visibility = View.VISIBLE
                fragmentBinding.taskViewDesc.visibility = View.VISIBLE
                fragmentBinding.viewTaskDueDateTime.visibility = View.VISIBLE
                fragmentBinding.etTaskDescription.visibility = View.GONE
                fragmentBinding.etTaskViewAbout.visibility = View.GONE
                fragmentBinding.etTaskViewName.visibility = View.GONE
                fragmentBinding.etTaskViewDue.visibility = View.GONE
                fragmentBinding.fabEditTask.setImageResource(R.drawable.ic_baseline_edit_24)

                if(yearSet != 0) {
                    val cal = Calendar.getInstance()
                    cal[Calendar.YEAR] = yearSet
                    cal[Calendar.MONTH] = monthSet
                    cal[Calendar.DAY_OF_MONTH] = daySet
                    cal[Calendar.HOUR_OF_DAY] = hourSet
                    cal[Calendar.MINUTE] = minuteSet
                    cal[Calendar.SECOND] = 0
                    cal[Calendar.MILLISECOND] = 0
                    // Create an instance of a task via constructor
                    task.dueDate = cal.time
                }

                // Commit to the task object then call the DAO
                task.name = fragmentBinding.etTaskViewName.text.toString()
                task.about = fragmentBinding.etTaskViewAbout.text.toString()
                task.description = fragmentBinding.etTaskDescription.text.toString()
                (activity as AppCompatActivity).supportActionBar?.title = task.name
                fragmentBinding.taskViewAbout.setText(task.about)
                fragmentBinding.taskViewDesc.setText(task.description)
                fragmentBinding.etTaskViewDue.setText(task.dueDate.toString().subSequence(0, 19))

                // Check what editing mode - subtask or task
                val parentProject = arguments?.getParcelable<Project>("parentProject")
                if(!task.isSubtask && parentProject == null) {
                    val taskDAO = TaskDAO(requireContext())
                    taskDAO.document = task
                    UserPreferences(requireContext()).getLoggedInUser()?.let { taskDAO.updateTask(it.authUid) }
                } else if(task.isSubtask && parentProject == null) {
                    val taskDAO = TaskDAO(requireContext())
                    val parentTask = arguments?.getParcelable<Task>("parentTask")!!
                    taskDAO.document = task
                    UserPreferences(requireContext()).getLoggedInUser()?.let { taskDAO.updateSubtask(it.authUid, parentTask.taskId) }
                } else if(parentProject != null) {
                    val taskDAO = TaskDAO(requireContext())
                    taskDAO.document = task
                    UserPreferences(requireContext()).getLoggedInUser()?.let {
                        taskDAO.updateProjectTask(it.authUid, parentProject.projectId)
                    }
                }

            } else {
                // Migrate Values
                fragmentBinding.etTaskViewName.setText(task.name)
                fragmentBinding.etTaskViewAbout.setText(task.about)
                fragmentBinding.etTaskDescription.setText(task.description)
                fragmentBinding.etTaskViewDue.setText(fragmentBinding.viewTaskDueDateTime.text.toString())

                // Set edit texts to visible, hide text views
                fragmentBinding.taskViewAbout.visibility = View.GONE
                fragmentBinding.taskViewDesc.visibility = View.GONE
                fragmentBinding.viewTaskDueDateTime.visibility = View.GONE
                fragmentBinding.etTaskDescription.visibility = View.VISIBLE
                fragmentBinding.etTaskViewAbout.visibility = View.VISIBLE
                fragmentBinding.etTaskViewName.visibility = View.VISIBLE
                fragmentBinding.etTaskViewDue.visibility = View.VISIBLE
                fragmentBinding.fabEditTask.setImageResource(R.drawable.ic_baseline_done_24)

            }

            // Flip the edit mode
            editMode = !editMode
        }

        this.fragmentBinding.fabNewSubtask.setOnClickListener { v ->
            // Pass the current Task object through a bundle
            val taskBundle = bundleOf(
                "taskObject" to this.task as Parcelable
            )
             findNavController().navigate(R.id.fromTaskViewToNewSubtask, taskBundle)
        }
        fragmentBinding.taskCheckboxIsDone.setOnClickListener { v ->
            val parentProject = arguments?.getParcelable<Project>("parentProject")
            if(!task.isSubtask && parentProject == null) {
                val taskDAO = TaskDAO(requireContext())
                task.completed = fragmentBinding.taskCheckboxIsDone.isChecked
                taskDAO.document = task
                UserPreferences(requireContext()).getLoggedInUser()?.let { taskDAO.updateTask(it.authUid) }
            } else if(task.isSubtask && parentProject == null) {
                val taskDAO = TaskDAO(requireContext())
                val parentTask = arguments?.getParcelable<Task>("parentTask")!!
                task.completed = fragmentBinding.taskCheckboxIsDone.isChecked
                taskDAO.document = task
                UserPreferences(requireContext()).getLoggedInUser()?.let { taskDAO.updateSubtask(it.authUid, parentTask.taskId) }
            } else if(parentProject != null) {
                val taskDAO = TaskDAO(requireContext())
                task.completed = fragmentBinding.taskCheckboxIsDone.isChecked
                taskDAO.document = task
                UserPreferences(requireContext()).getLoggedInUser()?.let {
                    taskDAO.updateProjectTask(it.authUid, parentProject.projectId)
                }
            }
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.view_task_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.option_delete_task -> {
                val parentProject = arguments?.getParcelable<Project>("parentProject")

                // Instantiate a DAO
                if(!this.task.isSubtask && parentProject == null) {
                    val taskDAO = TaskDAO(requireContext())
                    taskDAO.document = task
                    UserPreferences(requireContext()).getLoggedInUser()?.let { taskDAO.deleteTask(it.authUid) }
                } else if(this.task.isSubtask && parentProject == null) {
                    val taskDAO = TaskDAO(requireContext())
                    UserPreferences(requireContext()).getLoggedInUser()?.let {
                        val parentTask = arguments?.getParcelable<Task>("parentTask")!!
                        taskDAO.deleteSubtask(it.authUid, parentTask.taskId, task.taskId)
                    }
                } else if(parentProject != null) {
                    val taskDAO = TaskDAO(requireContext())
                    UserPreferences(requireContext()).getLoggedInUser()?.let {
                        taskDAO.deleteProjectTask(it.authUid, parentProject.projectId, task.taskId)
                    }
                }
            }

            R.id.option_upload -> {
                // Obtain the serverAuthCode of the google account, returns null if user is not a GoogleSignInAccount
                val loggedInAccount = GoogleSignIn.getLastSignedInAccount(requireContext())
                val serverAuthCode = userPreferences.getStringPreferences("serverAuthCode")
                val idToken = userPreferences.getStringPreferences("idToken")

                Log.d(TAG, "Server Auth Code: $serverAuthCode")

                // If logged in user is not a google account
                if (serverAuthCode.equals("")) {
                    Toast.makeText(context, "Failed to upload to Google Calendar. Please try signing in again with your Google Account.", Toast.LENGTH_SHORT).show()
                    return false
                }

                // Check if user has the necessary scopes
                val scope = Scope(CalendarScopes.CALENDAR)
                if (!GoogleSignIn.hasPermissions(loggedInAccount, scope)) {
                    Log.d(TAG, "No permissions yet")
                    GoogleSignIn.requestPermissions(activity, 120, loggedInAccount, scope)
                } else {
                    // Check if access token already exists in userPreferences
                    val accessToken = userPreferences.getStringPreferences("accessToken")
                    Log.d(TAG, "Access Token: ${accessToken.toString()}")
                    if (!(accessToken.equals(""))) {
                        // Build the calendar service
                        val credential = GoogleCredential().setAccessToken(accessToken)
                        val service = com.google.api.services.calendar.Calendar.Builder(NetHttpTransport(), JacksonFactory(), credential)
                            .setApplicationName("Teamwork")
                            .build()
                        /*
                        val service = Tasks.Builder(NetHttpTransport(), JacksonFactory(), credential)
                            .setApplicationName("Teamwork")
                            .build()

                         */

                        // Network requests cannot run on main UI thread, so we create a Worker thread.
                        Thread {
                            try {
                                // Instantiate a new Task for Task API
                                val newEvent = Event()
                                newEvent.summary = task.name
                                newEvent.start = EventDateTime().setDateTime(DateTime(task.dueDate))
                                newEvent.end = EventDateTime().setDateTime(DateTime(task.dueDate))

                                service.Events().insert("primary", newEvent).execute()
                                requireActivity().runOnUiThread {
                                    Toast.makeText(context, "Successfully uploaded to Google Calendar", Toast.LENGTH_SHORT).show()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                                requireActivity().runOnUiThread {
                                    Toast.makeText(context, "Failed to upload to Google Calendar", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }.start()
                    }
                    // Get access token from OAuth server
                    else {
                        Log.d(TAG, "${loggedInAccount.displayName}|${loggedInAccount.email}|${serverAuthCode}")

                        // Get access token from serverauthcode
                        val client = OkHttpClient()
                        val requestBody: RequestBody = FormEncodingBuilder()
                            .add("grant_type", "authorization_code")
                            .add(
                                "client_id",
                                getString(R.string.default_web_client_id)
                            )
                            .add(
                                "client_secret",
                                "0we2thAgoBHrYqbozOjT-3AI"
                            )
                            .add("redirect_uri", "")
                            .add("code", serverAuthCode)
                            .add("id_token", idToken)
                            .build()
                        val request: Request = Request.Builder()
                            .url("https://www.googleapis.com/oauth2/v4/token")
                            .post(requestBody)
                            .build()
                        client.newCall(request).enqueue(object : Callback {
                            override fun onFailure(request: Request?, e: IOException) {
                                Log.e(TAG, e.toString())
                                Toast.makeText(context, "Failed to upload to Google Calendar", Toast.LENGTH_SHORT).show()
                            }

                            @Throws(IOException::class)
                            override fun onResponse(response: Response) {
                                try {
                                    val jsonObject = JSONObject(response.body().string())
                                    val message: String = jsonObject.toString(5)
                                    Log.i(TAG, message)
                                    // Save access token to userPreferences
                                    val accessToken = jsonObject.getString("access_token")
                                    userPreferences.saveStringPreferences("accessToken", accessToken)

                                    Log.d(TAG, accessToken.toString())
                                    val credential = GoogleCredential().setAccessToken(accessToken)

                                    val service = com.google.api.services.calendar.Calendar.Builder(NetHttpTransport(), JacksonFactory(), credential)
                                        .setApplicationName("Teamwork")
                                        .build()
                                    /*
                                    val service = Tasks.Builder(NetHttpTransport(), JacksonFactory(), credential)
                                        .setApplicationName("Teamwork")
                                        .build()

                                     */

                                    // Network requests cannot run on main UI thread, so we create a Worker thread.
                                    Thread {
                                        try {
                                            // Instantiate a new Task for Task API
                                            val newEvent = Event()
                                            newEvent.summary = task.name
                                            newEvent.start = EventDateTime().setDateTime(DateTime(task.dueDate))
                                            newEvent.end = EventDateTime().setDateTime(DateTime(task.dueDate))

                                            service.Events().insert("primary", newEvent).execute()
                                            activity!!.runOnUiThread {
                                                Toast.makeText(context, "Successfully added to Google Calendar", Toast.LENGTH_SHORT).show()
                                            }

                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            activity!!.runOnUiThread {
                                                Toast.makeText(context, "Failed to upload to Google Calendar", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }.start()

                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    activity!!.runOnUiThread {
                                        Toast.makeText(context, "Failed to upload to Google Calendar", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        })
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        super.onStop()
    }
}