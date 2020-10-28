package com.matatov.movere.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.matatov.movere.R
import com.matatov.movere.interfaces.OnResultClickPosition
import com.matatov.movere.models.EventModel
import kotlinx.android.synthetic.main.element_list_event.view.*


class EventsRecyclerAdapter(private val onResultClickPosition: OnResultClickPosition.Event, private val arrayListEvents: ArrayList<EventModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return EventViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.element_list_event,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is EventsRecyclerAdapter.EventViewHolder -> {
                holder.bind(arrayListEvents.get(position))
                holder.itemView.setOnClickListener { onResultClickPosition!!.onClickPosition(arrayListEvents.get(position)) }
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayListEvents.size
    }

    class EventViewHolder(inflate: View) : RecyclerView.ViewHolder(inflate) {

        private val imageEvent = itemView.imageEvent
        private val textNameEvent = itemView.textNameEvent
        private val textDescEvent = itemView.textDescEvent

        fun bind(event: EventModel) {
            textNameEvent.text = event.eventName
            textDescEvent.text = event.eventDescription

            val requestOptions: RequestOptions = RequestOptions()
                .placeholder(R.drawable.ic_baseline_icon_user_24)
                .error(R.drawable.ic_baseline_icon_user_24)

            Glide.with(itemView.context)
                .setDefaultRequestOptions(requestOptions)
                .load(event.eventUriPhoto)
                .into(imageEvent)
        }
    }
}
