package com.stevechulsdev.bansa.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.stevechulsdev.sclog.ScLog

class DBManager {
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private fun setUserData(nickname: String, pw: String): HashMap<String, String> {
        return hashMapOf(
            "nickname" to nickname,
            "pw" to pw
        )
    }

    private fun insertUserData(uid: String, nickname: String, pw: String) {
        // Create a new user with a first and last name
        val userData = setUserData(nickname, pw)

        // Add a new document with a generated ID
        db.collection("user")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {
                ScLog.e(true, "success insert")
            }
            .addOnFailureListener { e ->
                ScLog.e(true, "Error adding document : $e")
            }
    }

    private fun readUserData(uid: String) {
        db.collection("user")
            .document(uid)
            .get()
            .addOnSuccessListener {
                ScLog.e(true, "${it.get("nickname")}")
            }
            .addOnFailureListener { exception ->
                ScLog.e(true, "Error getting documents : $exception")
            }
    }
}