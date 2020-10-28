package com.matatov.movere.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.matatov.movere.R
import com.matatov.movere.interfaces.OnResultClickPosition
import com.matatov.movere.models.UserModel
import kotlinx.android.synthetic.main.element_list_contact.view.*


class ContactsRecyclerAdapter(private val onResultClickPosition: OnResultClickPosition.Contact, private val arrayListContacts: ArrayList<UserModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ContactViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.element_list_contact,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ContactViewHolder -> {
                holder.bind(arrayListContacts.get(position))
                holder.itemView.setOnClickListener { onResultClickPosition!!.onClickPosition(arrayListContacts.get(position)) }
                holder.itemView.setOnLongClickListener {
                    onResultClickPosition!!.onClickPositionLongPress(arrayListContacts.get(position))
                    return@setOnLongClickListener true
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return arrayListContacts.size
    }

    class ContactViewHolder(inflate: View) : RecyclerView.ViewHolder(inflate) {

        private val imageContact = itemView.imageContact
        private val textNameContact = itemView.textNameContact

        fun bind(contact: UserModel) {
            textNameContact.text = contact.userName
            val requestOptions: RequestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_icon_user_24)
                .error(R.drawable.ic_baseline_icon_user_24)

            Glide.with(itemView.context)
                .setDefaultRequestOptions(requestOptions)
                .load(contact.userUriPhoto)
                .into(imageContact)
        }
    }
}
