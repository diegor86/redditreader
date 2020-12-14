package com.diegor.redditreader.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.CollapsingToolbarLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.diegor.redditreader.R
import com.diegor.redditreader.data.entities.Entry
import com.facebook.drawee.view.SimpleDraweeView

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [ItemListActivity]
 * in two-pane mode (on tablets) or a [ItemDetailActivity]
 * on handsets.
 */
class ItemDetailFragment : Fragment() {

    private var item: Entry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ENTRY_ITEM)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                item = it.getParcelable(ENTRY_ITEM)
                activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title = item?.author
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.item_detail, container, false)

        item?.let {
            rootView.findViewById<TextView>(R.id.entry_title).text = it.title
            rootView.findViewById<SimpleDraweeView>(R.id.entry_thumbnail).setImageURI(it.thumbnail)
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ENTRY_ITEM = "entry_item"
    }
}