package ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.utils

import ph.edu.dlsu.mobdeve.s17.sun.benedict.teamwork.model.Task
import retrofit2.Call
import retrofit2.http.*

interface GoogleTasksAPI {


    /**
     * Used to get the task list and find if the "Teamwork" task list already exists.
     */
    @GET
    fun getTaskLists() : Call<Any>

    /**
     * Required query parameters:
     * - taskList ID
     *
     * Required header information:
     * - Authentication token
     */
    @POST
    fun createTask(@Body task : Task) : Call<Task>
}