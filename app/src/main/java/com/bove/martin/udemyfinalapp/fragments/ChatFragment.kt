package com.bove.martin.udemyfinalapp.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager

import com.bove.martin.udemyfinalapp.R
import com.bove.martin.udemyfinalapp.adapters.ChatAdapter
import com.bove.martin.udemyfinalapp.model.Menssage
import com.bove.martin.udemyfinalapp.model.TotalMessagesEvent
import com.bove.martin.udemyfinalapp.utils.RxBus
import com.bove.martin.udemyfinalapp.utils.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.fragment_chat.view.*

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {
    private lateinit var _view: View

    private val store = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef: CollectionReference

    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private lateinit var adapter: ChatAdapter
    private val messageList: ArrayList<Menssage> = ArrayList()

    private var chatSuscription: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_chat, container, false)

        setupChatDataBase()
        setupCurrentUser()
        setupRecyclerView()
        setupChatButton()
        suscribeToChatMessages()

        return _view
    }

    private fun setupChatButton() {
        _view.imageButtonSend.setOnClickListener {
            val messageText = _view.editTextChat.text
            val photo = currentUser.photoUrl?.let { currentUser.photoUrl.toString() } ?: kotlin.run { "" }
            if (!messageText.isNullOrEmpty()) {
                val message = Menssage(currentUser.uid, messageText.toString(), photo, Date())
                saveMessage(message)
                _view.editTextChat.setText("")
            }
        }
    }

    private fun setupRecyclerView() {
        val lm = LinearLayoutManager(context)
        adapter = ChatAdapter(messageList, currentUser.uid)

        _view.reciclerViewChat.setHasFixedSize(true)
        _view.reciclerViewChat.layoutManager = lm
        _view.reciclerViewChat.itemAnimator = DefaultItemAnimator()
        _view.reciclerViewChat.adapter = adapter
    }

    private fun setupCurrentUser() {
        currentUser = mAuth.currentUser!!
    }

    private fun setupChatDataBase() {
       chatDBRef = store.collection("chat")
    }

    private fun saveMessage(message: Menssage) {
        val newMenssage = HashMap<String,Any>()
        newMenssage["authorID"] = message.authorID
        newMenssage["message"] = message.message
        newMenssage["profileImageUrl"] = message.profileImageUrl
        newMenssage["sendAt"] = message.sendAt

        chatDBRef.add(newMenssage)
            .addOnCompleteListener {
                activity!!.toast("El mensaje se añadio")
            }
            .addOnFailureListener {
                activity!!.toast("El mensaje no se añadio")
            }
    }

    private fun suscribeToChatMessages() {
        chatSuscription = chatDBRef
            .orderBy("sendAt", Query.Direction.ASCENDING)
            .limit(50)
            .addSnapshotListener(object : java.util.EventListener, EventListener<QuerySnapshot>{
            override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    activity!!.toast(exception.localizedMessage)
                    return
                }
                snapshot?.let{
                    messageList.clear()
                    val messages = it.toObjects(Menssage::class.java)
                    messageList.addAll(messages)
                    adapter.notifyDataSetChanged()
                    _view.reciclerViewChat.smoothScrollToPosition(messages.size)
                    RxBus.publish(TotalMessagesEvent(messages.size))
                }

            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        chatSuscription?.remove()
    }
}
