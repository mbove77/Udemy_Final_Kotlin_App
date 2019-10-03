package com.bove.martin.udemyfinalapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bove.martin.udemyfinalapp.R

import com.bove.martin.udemyfinalapp.utils.toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_info.view.*


/**
 * A simple [Fragment] subclass.
 */
class InfoFragment : Fragment() {

    private lateinit var _view: View

    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef: CollectionReference
    private var chatSuscription: ListenerRegistration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _view =  inflater.inflate(R.layout.fragment_info, container, false)

        setupDataBase()
        setupCurrentUser()
        setupCurrentUserInfoUI()
        suscribeTotalMessagesFirebase()

        return _view
    }

    private fun setupCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setupDataBase() {
        chatDBRef = store.collection("chat")
    }

    private fun setupCurrentUserInfoUI() {
        _view.textViewInfoemail.text = currentUser.email
        _view.textViewInfoName.text = currentUser.displayName?.let { currentUser.displayName } ?: run { getString(R.string.info_no_name) }

        currentUser.photoUrl?.let {
            Glide.with(this).load(currentUser.photoUrl).circleCrop().into(_view.imageViewInfoAvatar)
        } ?: run {
            Glide.with(this).load(R.drawable.ic_person).circleCrop().fitCenter().into(_view.imageViewInfoAvatar)
        }
    }

    private fun suscribeTotalMessagesFirebase() {
        chatDBRef.addSnapshotListener(object : java.util.EventListener, EventListener<QuerySnapshot> {
            override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    activity!!.toast(exception.localizedMessage)
                    return
                }
                snapshot?.let{
                   _view.textViewInfoTotalMsg.text = it.size().toString()
                }
            }
        })
    }

}
