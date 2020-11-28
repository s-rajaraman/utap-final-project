package edu.utap.rasriram.listman.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import edu.utap.rasriram.listman.R

class ProjectFragment: Fragment(R.layout.fragment_project) {
    val mAuth = FirebaseAuth.getInstance()
    val user = mAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



}