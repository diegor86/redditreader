package com.diegor.redditreader.ui.list

import android.os.Bundle
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.diegor.redditreader.ui.detail.ItemDetailActivity
import com.diegor.redditreader.R
import com.diegor.redditreader.data.entities.Entry
import com.diegor.redditreader.ui.util.InfiniteScrollListener
import com.diegor.redditreader.ui.util.MarginDecorator
import com.diegor.redditreader.ui.util.ScrollListener

import com.diegor.redditreader.util.result.EventObserver
import dagger.hilt.android.AndroidEntryPoint

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [ItemDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
@AndroidEntryPoint
class ItemListActivity : AppCompatActivity(), InfiniteScrollListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    private lateinit var adapter: EntryRecyclerViewAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private val viewModel by viewModels<EntryListViewModel>()

    private val entriesObserver = Observer<List<Entry>> {
        val movies = it ?: return@Observer

        adapter.submitList(movies)
    }

    private val loadingObserver = Observer<Boolean> {
        val loading = it ?: return@Observer

        swipeRefreshLayout.isRefreshing = loading
    }

    private val errorObserver = EventObserver<String> { error ->
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        if (findViewById<NestedScrollView>(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        setupRecyclerView(findViewById(R.id.item_list))

        swipeRefreshLayout = findViewById(R.id.swipe_refresh)
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.onRefreshTop()
        }

        viewModel.entryList.observe(this, entriesObserver)
        viewModel.loading.observe(this, loadingObserver)
        viewModel.errors.observe(this, errorObserver)

        viewModel.authenticateAndGetEntries()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addItemDecoration(MarginDecorator(this, R.dimen.recycler_item_margin))
        recyclerView.addOnScrollListener(ScrollListener(this, recyclerView.layoutManager as LinearLayoutManager))
        adapter = EntryRecyclerViewAdapter(
            this,
            twoPane
        )

        recyclerView.adapter = adapter
    }

    override fun onReachedBottom() {
        viewModel.onBottomReached()
    }
}