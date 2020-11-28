package edu.utap.rasriram.listman.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Project (
    var title: String? = null,
    var rowID: String = "",
    var tags: ArrayList<String>? = null
)
