package com.dsronne.testdewit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.dsronne.testdewit.storage.SqliteItemRepository
import com.dsronne.testdewit.storage.ItemStore
import com.dsronne.testdewit.ui.ItemPagerAdapter
import com.dsronne.testdewit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
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

        val repository = SqliteItemRepository(this)
        val itemStore = ItemStore(repository)
        // Seed initial program-management hierarchy only on first launch (empty DB)
        if (repository.find(com.dsronne.testdewit.datamodel.ItemId("root")) == null) {
            itemStore.initProgramManagement()
        }
        val rootChildren = itemStore.getChildrenOf(itemStore.root().id)

        binding.viewPager.adapter = ItemPagerAdapter(this, rootChildren, itemStore)
    }
}
