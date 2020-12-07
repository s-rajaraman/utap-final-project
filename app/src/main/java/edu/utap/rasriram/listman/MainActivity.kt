package edu.utap.rasriram.listman

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import edu.utap.rasriram.listman.view.ProjectFragment
import edu.utap.rasriram.listman.view.SearchFragment
import edu.utap.rasriram.listman.viewmodel.ProjectViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: ProjectViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val mAuth = FirebaseAuth.getInstance()

        val user = mAuth.currentUser
        if (user == null) {
            signIn()
        } else {
            viewModel.readTasks()
            viewModel.readProjects()
            val fragment = ProjectFragment()

            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_content, fragment)
                .commit()
        }
        actionBar?.title = "Projects"
        supportActionBar?.title = "Projects"
    }

    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RESULT_OK
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val fragment = ProjectFragment()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_content, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.actionbar, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_log_out -> {
                FirebaseAuth.getInstance().signOut()

                val providers = arrayListOf(
                    AuthUI.IdpConfig.EmailBuilder().build()
                )
                viewModel.readProjects()
                viewModel.readTasks()

                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                    RESULT_OK
                )
            }

            R.id.action_search -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_content, SearchFragment())
                    .addToBackStack("main")
                    .commit()

            }
        }
        return super.onOptionsItemSelected(item)
    }
}