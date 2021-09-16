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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.model.TaskList
import com.squareup.okhttp.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.R
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.dao.TaskDAO
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.databinding.FragmentViewTaskBinding
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task
import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils.UserPreferences
import java.io.IOException
import java.util.*

class SpecificTaskView: Fragment() {

    lateinit var fragmentBinding: FragmentViewTaskBinding
    private val TAG = "SpecificTaskView"

    lateinit var task: Task

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
            }
        }
    }

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

        // Apply to views
        Log.d("SpecificTaskView:onCreateView", task.isCompleted.toString())
        fragmentBinding.taskViewAbout.setText(task.about)
        fragmentBinding.taskViewDesc.setText(task.description)
        fragmentBinding.taskCheckboxIsDone.isChecked = task.isCompleted
        fragmentBinding.viewTaskDueDateTime.setText("${this.task.dueDate.toString().subSequence(0, 19)}")
        (activity as AppCompatActivity).supportActionBar?.title = task.name

        // Check if the task is overdue
        if(task.dueDate.before(Date())) {
            // Set colors to red
            fragmentBinding.viewTaskDueDateTime.setTextColor(requireContext().resources.getColor(R.color.warning_red))
        }

        // Register the Broadcast Receiver
        val intentFilter = IntentFilter()
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_user_task")
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.delete_user_task_failed")
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.update_user_task")
        intentFilter.addAction("ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.update_user_task_failed")
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

                // Commit to the task object then call the DAO
                task.name = fragmentBinding.etTaskViewName.text.toString()
                task.about = fragmentBinding.etTaskViewAbout.text.toString()
                task.description = fragmentBinding.etTaskDescription.text.toString()
                (activity as AppCompatActivity).supportActionBar?.title = task.name
                fragmentBinding.taskViewAbout.setText(task.about)
                fragmentBinding.taskViewDesc.setText(task.description)
                fragmentBinding.etTaskViewDue.setText(task.dueDate.toString().subSequence(0, 19))

                val taskDAO = TaskDAO(requireContext())
                taskDAO.document = task
                UserPreferences(requireContext()).getLoggedInUser()?.let { taskDAO.updateTask(it.authUid) }

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

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.view_task_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.option_delete_task -> {
                // Instantiate a DAO
                val taskDAO = TaskDAO(requireContext())
                taskDAO.document = task
                UserPreferences(requireContext()).getLoggedInUser()?.let { taskDAO.deleteTask(it.authUid) }
            }

            R.id.option_upload -> {
                // Obtain the Google Sign In Account
                val loggedInAccount = GoogleSignIn.getLastSignedInAccount(activity)
                val serverAuthCode = loggedInAccount.serverAuthCode

                if (serverAuthCode == null) {
                    Toast.makeText(context, "Failed to upload to Google Calendar: Server auth code = null", Toast.LENGTH_SHORT).show()
                    return false
                }

                Log.d(TAG, "Server Auth Code: $serverAuthCode")

                // Check if user has the necessary scopes
                val scope = Scope("https://www.googleapis.com/auth/tasks")
                if (!GoogleSignIn.hasPermissions(loggedInAccount, scope)) {
                    Log.d(TAG, "No permissions yet")
                    GoogleSignIn.requestPermissions(activity, 120, loggedInAccount, scope)
                } else {
                    // Build the tasks service
                    Log.d(TAG, "${loggedInAccount.displayName}|${loggedInAccount.email}|${loggedInAccount.serverAuthCode}")

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
                        .add("id_token", loggedInAccount.idToken)
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
                                val accessToken = jsonObject.getString("access_token")
                                Log.d(TAG, accessToken)
                                val credential = GoogleCredential().setAccessToken(accessToken)


                                val service = Tasks.Builder(NetHttpTransport(), JacksonFactory(), credential)
                                    .setApplicationName("Teamwork")
                                    .build()

                                // Network requests cannot run on main UI thread, so we create a Worker thread.
                                Thread {
                                    try {
                                        // Instantiate a new Task for Task API
                                        val newTask = com.google.api.services.tasks.model.Task()
                                        newTask.title = task.name

                                        // Get all tasks list of user
                                        val result = service.tasklists().list().execute()
                                        var jsonObject = JSONObject(result.toString())
                                        var targetTaskList : TaskList? = null
                                        Log.d(TAG, jsonObject.toString())
                                        val jsonArray = JSONArray(jsonObject.getString("items"))

                                        // Check if the user has a task list with a title of Teamwork
                                        for (i in 0 until jsonArray.length()) {
                                            val list : JSONObject = jsonArray.getJSONObject(i)
                                            Log.d(TAG, list.toString())

                                            // Retrieve the task list's id
                                            if (list.getString("title").equals("Teamwork")) {
                                                targetTaskList = TaskList()
                                                targetTaskList.id = list.getString("id")
                                                break
                                            }
                                        }

                                        // Create a new list and upload the list in the newly created task list
                                        if (targetTaskList == null) {
                                            targetTaskList = TaskList()
                                            targetTaskList.title = "Teamwork"
                                            Log.d(TAG, "Adding new task list")

                                            // Retrieve the new task list id
                                            val newListResult = service.tasklists().insert(targetTaskList).execute()
                                            jsonObject = JSONObject(newListResult.toString())
                                            val taskListId = jsonObject.getString("id")
                                            Log.d(TAG, "New task list id: $taskListId")

                                            // Add this task to task list
                                            Log.d(TAG, "Adding new task")
                                            service.tasks().insert(taskListId, newTask).execute()
                                            activity!!.runOnUiThread {
                                                Toast.makeText(context, "Successfully uploaded to Google Calendar", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        // Add task to existing list
                                        else {
                                            Log.d(TAG, "Adding new task")
                                            service.tasks().insert(targetTaskList.id, newTask).execute()
                                            activity!!.runOnUiThread {
                                                Toast.makeText(context, "Successfully uploaded to Google Calendar", Toast.LENGTH_SHORT).show()
                                            }
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
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(broadcastReceiver)
        super.onStop()
    }
}