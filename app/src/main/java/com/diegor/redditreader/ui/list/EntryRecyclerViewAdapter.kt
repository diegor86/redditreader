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
import com.diegor.redditreader.dummy.DummyContent
import com.diegor.redditreader.ui.detail.ItemDetailActivity
import com.diegor.redditreader.ui.detail.ItemDetailFragment

class EntryRecyclerViewAdapter(private val parentActivity: ItemListActivity,
                               private val twoPane: Boolean) :
        ListAdapter<Entry, EntryRecyclerViewAdapter.ViewHolder>(createEntryDiffCallback()) {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            if (twoPane) {
                val fragment = ItemDetailFragment()
                    .apply {
                    arguments = Bundle().apply {
                        putString(ItemDetailFragment.ARG_ITEM_ID, DummyContent.ITEMS.first().id)
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
                    putExtra(ItemDetailFragment.ARG_ITEM_ID, DummyContent.ITEMS.first().id)
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
        holder.idView.text = item.name
        holder.contentView.text = item.title

        with(holder.itemView) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount() = currentList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(
            R.id.id_text
        )
        val contentView: TextView = view.findViewById(
            R.id.content
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