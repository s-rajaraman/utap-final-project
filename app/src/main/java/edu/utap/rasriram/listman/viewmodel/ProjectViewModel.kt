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

private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

class ProjectViewModel(application: Application, private val state: SavedStateHandle) :
    AndroidViewModel(application) {

    val mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser

    private var projects = MutableLiveData<List<Project>>()

    companion object { }

    fun saveProject(project: Project) {
        if (user != null) {
            project.rowID = db.collection("projects").document().id
            db.collection("projects/${user.uid}/project").add(project)
        }
    }


    fun readProjects() {
        if (user != null) {
            db
            .collection("projects")
            .get()
            .addOnSuccessListener { result ->
                projects.value = result.documents.mapNotNull {
                    it.toObject(Project::class.java)
                }
            }
        }
    }

    fun observeProjects(): MutableLiveData<List<Project>> {
        return projects

    }
}