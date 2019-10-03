package com.bove.martin.udemyfinalapp.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bove.martin.udemyfinalapp.R
import com.bove.martin.udemyfinalapp.model.Menssage
import com.bove.martin.udemyfinalapp.utils.inflate
import com.bove.martin.udemyfinalapp.utils.loadFromUrl
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_chat_item_left.view.*
import kotlinx.android.synthetic.main.fragment_chat_item_right.view.*
import java.text.SimpleDateFormat

/**
 * Created by Martín Bove on 21/09/2019.
 * E-mail: mbove77@gmail.com
 */

class ChatAdapter(val items: List<Menssage>, val userId: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val GLOBAL_MESSAGE = 1
    private val MY_MESSAGE = 2
    private val LAYOUT_RIGHT = R.layout.fragment_chat_item_right
    private val LAYOUT_LEFT = R.layout.fragment_chat_item_left

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        if (items[position].authorID == userId) {
            return MY_MESSAGE
        } else {
            return GLOBAL_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            MY_MESSAGE -> ViewHolderR(parent.inflate(LAYOUT_RIGHT))
            else -> ViewHolderL(parent.inflate(LAYOUT_LEFT))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType) {
            MY_MESSAGE -> (holder as ViewHolderR).bind(items[position])
            GLOBAL_MESSAGE -> (holder as ViewHolderL).bind(items[position])
        }
    }

    class ViewHolderR(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(menssage: Menssage) = with(itemView) {
            textViewMessageRight.text = menssage.message
            textViewTimeRight.text = SimpleDateFormat("hh:mm").format(menssage.sendAt)
            if(menssage.profileImageUrl.isEmpty()) {
                Glide.with(this).load(R.drawable.ic_person).into(imageViewProfileRight)
            } else {
                Glide.with(this).load(menssage.profileImageUrl).circleCrop().into(imageViewProfileRight)
            }

        }
    }
    class ViewHolderL(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(menssage: Menssage) = with(itemView) {
            textViewMessageLeft.text = menssage.message
            textViewTimeLeft.text = SimpleDateFormat("hh:mm").format(menssage.sendAt)
            if(menssage.profileImageUrl.isEmpty()) {
                Glide.with(this).load(R.drawable.ic_person).into(imageViewProfileLeft)
            } else {
                Glide.with(this).load(menssage.profileImageUrl).circleCrop().into(imageViewProfileLeft)
            }
        }
    }

}

