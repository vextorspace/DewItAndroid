package com.dsronne.testdewit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.dsronne.testdewit.storage.InMemoryItemRepository
import com.dsronne.testdewit.storage.ItemStore
import com.dsronne.testdewit.ui.ItemPagerAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val repository = InMemoryItemRepository()
        val itemStore = ItemStore(repository)
        val rootChildren = itemStore.getChildrenOf(itemStore.root().id)

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        viewPager.adapter = ItemPagerAdapter(this, rootChildren)
    }
}
