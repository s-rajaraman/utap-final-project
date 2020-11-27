package edu.utap.rasriram.listman

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
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
        actionBar?.title = "Projects"
        supportActionBar?.title = "Projects"
    }

}