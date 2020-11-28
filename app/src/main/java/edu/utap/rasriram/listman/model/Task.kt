package edu.utap.rasriram.listman.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Task(
    var title: String? = null,
    var description: String? = null,
    @ServerTimestamp val deadline: Timestamp? = null,
    var rowID: String = "",
    var tags: ArrayList<String>? = null,
    var projectId: String = ""
)