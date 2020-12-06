package edu.utap.rasriram.listman

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.Wearable

class MainActivity : WearableActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on
        setAmbientEnabled()

        val dataClient = Wearable.getDataClient(this)
        dataClient.addListener {
            for (i in it) {
                Log.d("Tag", i.dataItem.toString())
            }
        }
    }
}