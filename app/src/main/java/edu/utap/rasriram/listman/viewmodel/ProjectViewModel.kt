package edu.utap.rasriram.listman.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.rasriram.listman.model.Project
import edu.utap.rasriram.listman.model.Task

private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

class ProjectViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(application) {

    val mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser

    private var projects = MutableLiveData<List<Project>>()
    private var tasks = MutableLiveData<List<Task>>()

    private var search = MutableLiveData<String>()
    private var searchResults = MutableLiveData<List<Task>>()

    companion object {}

    fun saveProject(project: Project) {
        if (user != null) {
            if (project.rowID == "") {
                project.rowID = db.collection("projects").document().id
            }
            db.collection("projects/${user.uid}/project").document(project.rowID).set(project)
            readProjects()
        }
    }

    fun getRowId(): String {
        return db.collection("projects").document().id
    }


    fun readProjects() {
        if (user != null) {
            db
                .collection("projects/${user.uid}/project")
                .get()
                .addOnSuccessListener { result ->
                    val projectList = result.documents.mapNotNull {
                        val obj = it.toObject(Project::class.java)
                        Log.d("", "${obj?.title}")
                        obj
                    }
                    projects.postValue(projectList)
                }
        } else {
            projects.postValue(arrayListOf())
        }
    }

    fun observeProjects(): MutableLiveData<List<Project>> {
        return projects

    }

    fun removeProject(project: Project) {
        if (user != null) {
            db
                .collection("projects/${user.uid}/project")
                .document(project.rowID)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                    readProjects()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
    }

    fun saveTask(task: Task) {
        if (user != null) {
            if (task.rowID == "") {
                task.rowID = db.collection("tasks").document().id
            }
            db.collection("projects/${user.uid}/task").document(task.rowID).set(task)
            readTasks()
        }
    }

    fun readTasks() {
        if (user != null) {
            db
                .collection("projects/${user.uid}/task")
                .get()
                .addOnSuccessListener { result ->
                    val taskList = result.documents.mapNotNull {
                        val obj = it.toObject(Task::class.java)
                        Log.d("", "${obj?.title}")
                        obj
                    }
                    tasks.postValue(taskList)
                }
        } else {
            tasks.postValue(arrayListOf())
        }
    }

    fun observeTasks(): MutableLiveData<List<Task>> {
        return tasks
    }

    fun removeTask(task: Task) {
        if (user != null) {
            db
                .collection("projects/${user.uid}/task")
                .document(task.rowID)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                    readTasks()
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }

    }

    private fun filterProjectWithMatchingTag(searchTerm: String, project: Project): Boolean {
        for (t in project.tags) {
            if (t.contains(searchTerm)) {
                return true
            }
        }
        return false
    }

    private fun filterTaskBySearch(searchTerm: String, task: Task, projectIds: List<String>): Boolean {
       return projectIds.contains(task.projectId) || task.title?.contains(searchTerm) == true
    }

    fun updateSearch(str: String) {
        search.postValue(str)

        if(str == ""){
            searchResults.postValue(arrayListOf())
            return
        }

        val projectsWithMatchingTags =
            projects
                .value
                ?.filter { filterProjectWithMatchingTag(str, it) }
                ?.map { it.rowID }
                ?: arrayListOf()


        val matchedTasks = tasks.value?.filter { filterTaskBySearch(str, it, projectsWithMatchingTags) }

       searchResults.postValue(matchedTasks)

    }

    fun observeSearchResults(): MutableLiveData<List<Task>> {
        return searchResults
    }
}