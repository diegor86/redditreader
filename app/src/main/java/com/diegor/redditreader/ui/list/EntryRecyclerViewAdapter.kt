package com.diegor.redditreader.ui.list

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegor.redditreader.R
import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.util.result.formatTimeAgo
import com.facebook.drawee.view.SimpleDraweeView

interface OnEntryTappedListener {
    fun onEntryTapped(entry: Entry)
    fun onEntryDismissed(entry: Entry)
}

class EntryRecyclerViewAdapter(private val onEntryTappedListener: OnEntryTappedListener?) :
        ListAdapter<Entry, EntryRecyclerViewAdapter.ViewHolder>(createEntryDiffCallback()) {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val entry = v.tag as? Entry

            entry?.let {
                onEntryTappedListener?.onEntryTapped(entry)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_list_content, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.author.text = item.author
        holder.title.text = item.title
        if (item.markedAsRead) {
            holder.title.typeface = Typeface.create(holder.title.typeface, Typeface.NORMAL)
        } else {
            holder.title.typeface = Typeface.create(holder.title.typeface, Typeface.BOLD)
        }
        holder.created.text = item.created.formatTimeAgo(holder.itemView.context)
        holder.comments.text = holder.itemView.context.resources.getQuantityString(R.plurals.number_of_comments, item.comments, item.comments)
        holder.thumbnail.setImageURI(item.thumbnail)

        holder.dismiss.setOnClickListener {
            onEntryTappedListener?.onEntryDismissed(item)
        }

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = currentList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(
            R.id.entry_title
        )

        val author: TextView = view.findViewById(
            R.id.entry_author
        )

        val created: TextView = view.findViewById(
            R.id.created
        )

        val comments: TextView = view.findViewById(
            R.id.comments
        )

        val dismiss: ImageButton = view.findViewById(
            R.id.dismiss_entry
        )

        val thumbnail: SimpleDraweeView = view.findViewById(
            R.id.entry_thumbnail
        )
    }

    companion object {
        private fun createEntryDiffCallback() = object : DiffUtil.ItemCallback<Entry>() {
            override fun areItemsTheSame(oldItem: Entry, newItem: Entry): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Entry, newItem: Entry): Boolean {
                return oldItem == newItem
            }
        }
    }
}