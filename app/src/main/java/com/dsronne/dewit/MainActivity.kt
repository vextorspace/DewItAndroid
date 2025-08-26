package com.dsronne.dewit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.dsronne.dewit.storage.SqliteItemRepository
import com.dsronne.dewit.storage.ItemStore
import com.dsronne.dewit.ui.ItemPagerAdapter
import com.dsronne.dewit.ui.ItemStoreProvider
import com.dsronne.dewit.databinding.ActivityMainBinding
import com.dsronne.dewit.datamodel.ItemId

class MainActivity : AppCompatActivity(), ItemStoreProvider {
    private val itemStore: ItemStore by lazy {
        val repository = SqliteItemRepository(applicationContext)
        val store = ItemStore(repository)
        if (repository.find(ItemId("root")) == null) {
            store.initProgramManagement()
        }
        store
    }
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

        val rootChildren = itemStore.getChildrenOf(itemStore.root().id)

        binding.viewPager.adapter = ItemPagerAdapter(this, rootChildren)
    }

    override fun itemStore(): ItemStore = itemStore
}
