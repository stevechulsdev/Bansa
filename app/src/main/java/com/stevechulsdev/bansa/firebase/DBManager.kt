package com.stevechulsdev.bansa.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.stevechulsdev.sclog.ScLog

class DBManager {
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private fun setUserData(nickname: String): HashMap<String, String> {
        return hashMapOf(
            "nickname" to nickname
        )
    }

    fun insertUserData(uid: String, nickname: String, onStatusListener: OnInsertStatusListener) {
        // Create a new UserData with a first and last name
        val userData = setUserData(nickname)

        // Add a new document with a generated ID
        db.collection("UserData")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                ScLog.e(true, "success insert")
                onStatusListener.onSuccess()
            }
            .addOnFailureListener { e ->
                ScLog.e(true, "Error adding document : $e")
                onStatusListener.onFail(e)
            }
    }

    fun readUserData(uid: String, onStatusListener: OnReadStatusListener) {
        db.collection("UserData")
            .document(uid)
            .get()
            .addOnSuccessListener {
                ScLog.e(true, "uid : ${it.id}, nickname : ${it.data?.get("nickname")}")
                onStatusListener.onSuccess(it.id, it.data?.get("nickname").toString())
            }
            .addOnFailureListener { exception ->
                ScLog.e(true, "Error getting documents : $exception")
            }
    }

    interface OnInsertStatusListener {
        fun onSuccess()
        fun onFail(e: Exception)
    }

    interface OnReadStatusListener {
        fun onSuccess(uid: String, nickname: String)
        fun onFail(e: Exception)
    }
}