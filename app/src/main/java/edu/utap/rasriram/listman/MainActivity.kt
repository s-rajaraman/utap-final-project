package edu.utap.rasriram.listman

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import edu.utap.rasriram.listman.view.ProjectFragment
import edu.utap.rasriram.listman.viewmodel.ProjectViewModel

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel: ProjectViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val mAuth = FirebaseAuth.getInstance()

        val user = mAuth.currentUser
        if (user == null) {
            val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build())

            // Create and launch sign-in intent
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RESULT_OK)
        }
        else {
            val fragment = ProjectFragment()

            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_content, fragment)
                .addToBackStack("home")
                .commit()
        }
        actionBar?.title = "Projects"
        supportActionBar?.title = "Projects"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = ProjectFragment()

        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_content, fragment)
            .commit()
    }
}