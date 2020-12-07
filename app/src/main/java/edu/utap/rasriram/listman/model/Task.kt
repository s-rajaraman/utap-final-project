package edu.utap.rasriram.listman.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Task(
    var title: String? = null,
    var description: String? = null,
    @ServerTimestamp val updateTimestamp: Timestamp? = null,
    var rowID: String = "",
    var tags: ArrayList<String> = arrayListOf(),
    var projectId: String = "",
    val creationTime: Long = System.currentTimeMillis()
)
