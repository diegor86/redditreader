package com.diegor.redditreader.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.diegor.redditreader.R
import com.diegor.redditreader.data.entities.Entry
import com.facebook.drawee.view.SimpleDraweeView
import dagger.hilt.android.AndroidEntryPoint

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListActivity]
 * in two-pane mode (on tablets) or a [ItemDetailActivity]
 * on handsets.
 */
@AndroidEntryPoint
class ItemDetailFragment : Fragment() {

    private val viewModel by viewModels<EntryDetailViewModel>()

    private lateinit var title: TextView
    private lateinit var author: TextView
    private lateinit var thumbnail: SimpleDraweeView

    private val entryObserver = Observer<Entry> {
        val entry = it ?: return@Observer

        author.text = entry.author
        title.text = entry.title
        thumbnail.setImageURI(entry.thumbnail)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getParcelable<Entry>(ENTRY_ITEM)?.let {
            viewModel.setEntry(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.item_detail, container, false)


        title = rootView.findViewById(R.id.entry_title)
        author = rootView.findViewById(R.id.entry_author)
        thumbnail = rootView.findViewById(R.id.entry_thumbnail)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.entry.observe(viewLifecycleOwner, entryObserver)
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ENTRY_ITEM = "entry_item"
    }
}