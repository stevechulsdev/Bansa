package com.stevechulsdev.bansa.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.stevechulsdev.bansa.etc.LocalPreference
import com.stevechulsdev.sclog.ScLog
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

    fun insertReply(postingId: String, message: String, insertReplyListener: () -> Unit) {
        val replyData = hashMapOf(
            "nickName" to LocalPreference.userNickName,
            "message" to message,
            "timeStamp" to Date().time.toString()
        )
        db.collection("PostingList")
            .document(postingId)
            .update("replyList", FieldValue.arrayUnion(replyData))
            .addOnSuccessListener {
                insertReplyListener.invoke()
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun getReplyList(postingId: String, getReplyListListener: (ArrayList<HashMap<String, String>>) -> Unit) {
        val replayList = ArrayList<HashMap<String, String>>()
        db.collection("PostingList")
            .document(postingId)
            .get()
            .addOnSuccessListener {
                it.data?.get("replyList")?.let {
                    for(data in (it as ArrayList<HashMap<String, String>>)) {
                        replayList.add(data)
                    }

                    getReplyListListener.invoke(replayList)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun getReplyCount(postingId: String, getReplyCountListener: (Int) -> Unit) {
        db.collection("PostingList")
            .document(postingId)
            .get()
            .addOnSuccessListener {
                it.data?.get("replyList")?.let {
                    val size = (it as ArrayList<HashMap<String, String>>).size
                    getReplyCountListener.invoke(size)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun insertBookMark(postingId: String, insertBookMarkListener: () -> Unit) {
        db.collection("UserData")
            .document(LocalPreference.userUid)
            .update("bookMarkList", FieldValue.arrayUnion(postingId))
            .addOnSuccessListener {
                insertBookMarkListener.invoke()
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun deleteBookMark(postingId: String, deleteBookMarkListener: () -> Unit) {
        db.collection("UserData")
            .document(LocalPreference.userUid)
            .update("bookMarkList", FieldValue.arrayRemove(postingId))
            .addOnSuccessListener {
                deleteBookMarkListener.invoke()
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

    fun setBookMark(postingId: String, bookMarkListener: (Boolean) -> Unit) {
        db.collection("PostingList")
            .document(postingId)
            .get()
            .addOnSuccessListener {
                for(uid in (it.data?.get("bookMarkList") as ArrayList<String>)) {
                    if(uid == LocalPreference.userUid) {
                        bookMarkListener.invoke(true)
                        return@addOnSuccessListener
                    }
                }

                bookMarkListener.invoke(false)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun onBookMark(postingId: String, bookMarkListener: () -> Unit) {
        db.collection("PostingList")
            .document(postingId)
            .update("bookMarkList", FieldValue.arrayUnion(LocalPreference.userUid))
            .addOnSuccessListener {
                bookMarkListener.invoke()
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun offBookMark(postingId: String, bookMarkListener: () -> Unit) {
        db.collection("PostingList")
            .document(postingId)
            .update("bookMarkList", FieldValue.arrayRemove(LocalPreference.userUid))
            .addOnSuccessListener {
                bookMarkListener.invoke()
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun setLike(postingId: String, likeListener: (Boolean) -> Unit) {
        db.collection("PostingList")
            .document(postingId)
            .get()
            .addOnSuccessListener {
                for(uid in (it.data?.get("heartList") as ArrayList<String>)) {
                    if(uid == LocalPreference.userUid) {
                        likeListener.invoke(true)
//                        offLike(postingId)
                        return@addOnSuccessListener
                    }
                }

                likeListener.invoke(false)
//                onLike(postingId)
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun onLike(postingId: String, likeListener: () -> Unit) {
        db.collection("PostingList")
            .document(postingId)
            .update("heartList", FieldValue.arrayUnion(LocalPreference.userUid))
            .addOnSuccessListener {
                likeListener.invoke()
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    fun offLike(postingId: String, likeListener: () -> Unit) {
        db.collection("PostingList")
            .document(postingId)
            .update("heartList", FieldValue.arrayRemove(LocalPreference.userUid))
            .addOnSuccessListener {
                likeListener.invoke()
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