package com.diegor.redditreader.ui.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegor.redditreader.R
import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.ui.detail.ItemDetailActivity
import com.diegor.redditreader.ui.detail.ItemDetailFragment
import com.diegor.redditreader.util.result.formatTimeAgo
import com.facebook.drawee.view.SimpleDraweeView

class EntryRecyclerViewAdapter(private val parentActivity: ItemListActivity,
                               private val twoPane: Boolean) :
        ListAdapter<Entry, EntryRecyclerViewAdapter.ViewHolder>(createEntryDiffCallback()) {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val entry = v.tag as? Entry

            if (twoPane) {
                val fragment = ItemDetailFragment()
                    .apply {
                    arguments = Bundle().apply {
                        putParcelable(ItemDetailFragment.ENTRY_ITEM, entry)
                    }
                }
                parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit()
            } else {
                val intent = Intent(
                    v.context,
                    ItemDetailActivity::class.java
                ).apply {
                    putExtra(ItemDetailFragment.ENTRY_ITEM, entry)
                }
                v.context.startActivity(intent)
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
        holder.created.text = item.created.formatTimeAgo(holder.itemView.context)
        holder.comments.text = holder.itemView.context.resources.getQuantityString(R.plurals.number_of_comments, item.comments, item.comments)
        holder.thumbnail.setImageURI(item.thumbnail)

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