package com.dsronne.dewit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.dsronne.dewit.storage.SqliteItemRepository
import com.dsronne.dewit.storage.ItemStore
import com.dsronne.dewit.ui.ItemPagerAdapter
import com.dsronne.dewit.ui.ItemStoreProvider
import com.dsronne.dewit.ui.RootPagerController
import com.dsronne.dewit.databinding.ActivityMainBinding
import com.dsronne.dewit.datamodel.ItemId

class MainActivity : AppCompatActivity(), ItemStoreProvider, RootPagerController {
    private val itemStore: ItemStore by lazy {
        val repository = SqliteItemRepository(applicationContext)
        val store = ItemStore(repository)
        if (repository.find(ItemId("root")) == null) {
            store.initProgramManagement()
        }
        store
    }
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: ItemPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.topAppBar)
        viewPager = binding.viewPager
        val rootChildren = itemStore.getChildrenOf(itemStore.root().id)
        pagerAdapter = ItemPagerAdapter(this, rootChildren)
        viewPager.adapter = pagerAdapter

        // Keep pages in sync with store changes (adds/moves/removals) without
        // touching the adapter during layout passes.
        itemStore.addChangeListener {
            val snapshot = pagerAdapter.itemsSnapshot()
            val snapshotIds = snapshot.map { it.id }
            val newItems = itemStore.getChildrenOf(itemStore.root().id)
            val newIds = newItems.map { it.id }
            if (newIds != snapshotIds) {
                viewPager.post {
                    val currentId = snapshot.getOrNull(viewPager.currentItem)?.id
                    pagerAdapter.submitItems(newItems)
                    val newIndex = currentId?.let { id -> pagerAdapter.indexOf(id) } ?: -1
                    if (newIndex != -1) {
                        viewPager.setCurrentItem(newIndex, false)
                    } else if (newItems.isNotEmpty()) {
                        val safeIndex = minOf(viewPager.currentItem, newItems.lastIndex)
                        viewPager.setCurrentItem(safeIndex, false)
                    }
                }
            }
        }
    }

    override fun itemStore(): ItemStore = itemStore

    override fun onRootChildRemoved(removedId: ItemId) {
        viewPager.post {
            val newItems = itemStore.getChildrenOf(itemStore.root().id)
            pagerAdapter.submitItems(newItems)
            if (newItems.isNotEmpty()) {
                val current = minOf(viewPager.currentItem, newItems.lastIndex)
                viewPager.setCurrentItem(current, false)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_top_level -> {
                addTopLevelItem()
                true
            }
            R.id.action_init_program_mgmt -> {
                showInitProgramManagementDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showInitProgramManagementDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.confirm_init_title)
            .setMessage(R.string.confirm_init_message)
            .setPositiveButton(R.string.confirm) { _, _ ->
                itemStore.initProgramManagement()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun addTopLevelItem() {
        val child = com.dsronne.dewit.datamodel.ListItem(com.dsronne.dewit.datamodel.Item("new item"))
        itemStore.add(child)
        val root = itemStore.root()
        root.add(child)
        itemStore.edit(root)
        val newItems = itemStore.getChildrenOf(root.id)
        pagerAdapter.submitItems(newItems)
        val idx = pagerAdapter.indexOf(child.id)
        if (idx != -1) viewPager.setCurrentItem(idx, true)
    }
}
