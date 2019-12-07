package com.stevechulsdev.bansa.firebase

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.sclog.ScLog

class DBManager {
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private fun setUserData(nickname: String, loginType: String): HashMap<String, String> {
        return hashMapOf(
            "nickname" to nickname,
            "loginType" to loginType
        )
    }

    private fun setBookMarkData(postingId: String): String {
        return postingId
    }

    fun insertUserData(uid: String, nickname: String, loginType: String, onStatusListener: OnInsertStatusListener) {

        // Create a new UserData with a first and last name
        val userData = setUserData(nickname, loginType)

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

    fun checkUserData(uid: String, onStatusListener: OnCheckStatusListener) {
        db.collection("UserData")
            .get()
            .addOnSuccessListener {
                if(it.documents.size <= 0) {
                    onStatusListener.onSuccess(false, uid, "")
                    return@addOnSuccessListener
                }

                for(document in it.documents) {
                    if(document.id == uid) {
                        onStatusListener.onSuccess(true, document.id, document.get("nickname").toString())
                        return@addOnSuccessListener
                    }
                }

                onStatusListener.onSuccess(false, uid, "")
            }
            .addOnFailureListener { exception ->
                ScLog.e(true, "Error getting documents : $exception")
            }
    }

    fun getUserData(uid: String) {
        db.collection("UserData")
            .document(uid)
            .get()
            .addOnSuccessListener {

            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun setBookMark(uid: String, postingId: String) {
        db.collection("UserData")
            .document(uid)
            .update("bookMarkList", FieldValue.arrayUnion(postingId))
            .addOnSuccessListener {
                ScLog.e(Constants.IS_DEBUG, "북마크 추가 완료")
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun getUserBookMarkData(bookMarkListener: (ArrayList<String>) -> Unit) {
        val bookMarkList = ArrayList<String>()

        db.collection("UserData")
            .document(LocalPreference.userUid)
            .get()
            .addOnSuccessListener {
                for(data in (it.data?.get("bookMarkList") as ArrayList<String>)) {
                    bookMarkList.add(data)
                }

                bookMarkListener.invoke(bookMarkList)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun getUserBookMarkList(postingId: String, bookMarkListener: (DocumentSnapshot) -> Unit) {
        db.collection("PostingList")
            .document(postingId)
            .get()
            .addOnSuccessListener {
                bookMarkListener.invoke(it)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    interface OnInsertStatusListener {
        fun onSuccess()
        fun onFail(e: Exception)
    }

    interface OnCheckStatusListener {
        fun onSuccess(isMember: Boolean, uid: String, nickname: String)
        fun onFail(e: Exception)
    }
}