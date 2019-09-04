package com.stevechulsdev.bansa.firebase

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.stevechulsdev.bansa.etc.Constants
import com.stevechulsdev.bansa.main.model.Posting
import com.stevechulsdev.bansa.main.view.MainListActivity
import com.stevechulsdev.sclog.ScLog
import org.jetbrains.anko.startActivity

class DBManager {
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private fun setUserData(nickname: String, loginType: String): HashMap<String, String> {
        return hashMapOf(
            "nickname" to nickname,
            "loginType" to loginType
        )
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

    fun getPostingData(context: Context) {
        db.collection("Posting")
            .get()
            .addOnSuccessListener {
                for (postingData in it.documents) {

                    Posting?.apply {
                        imagePath = postingData.get("imagePath").toString()
                        description = postingData.get("description").toString()
                        link = postingData.get("link").toString()
                        tagList.addAll((postingData.get("tag") as List<String>))

                        ScLog.e(Constants.IS_DEBUG, "imagePath : $imagePath")
                        ScLog.e(Constants.IS_DEBUG, "description : $description")
                        ScLog.e(Constants.IS_DEBUG, "link : $link")
                        ScLog.e(Constants.IS_DEBUG, "tagList : ${tagList[0]}")
                        ScLog.e(Constants.IS_DEBUG, "tagList : ${tagList[1]}")

                        context.startActivity<MainListActivity>()
                    }
                }
            }
            .addOnFailureListener { exception ->
                ScLog.e(Constants.IS_DEBUG, "getPostingData error : $exception")
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