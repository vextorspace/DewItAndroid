package com.dsronne.dewit.quickadd

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.di.StoreLocator

class QuickAddActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val text = extractText(intent)
        if (!text.isNullOrBlank()) {
            val store = StoreLocator.itemStore(applicationContext)
            // Prefer inbox if present; fallback to root
            val inbox = store.find(ItemId("inbox")) ?: store.root()
            val child = com.dsronne.dewit.datamodel.ListItem(Item(text))
            store.add(child)
            inbox.add(child)
            store.edit(inbox)
        }
        // Finish quickly without UI
        finish()
    }

    private fun extractText(intent: Intent?): String? {
        if (intent == null) return null
        // Assistant CREATE_NOTE typically uses EXTRA_TEXT
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let { if (it.isNotBlank()) return it }
        // Fallbacks
        intent.getStringExtra(Intent.EXTRA_TITLE)?.let { if (it.isNotBlank()) return it }
        if (Intent.ACTION_VIEW == intent.action) {
            val data: Uri? = intent.data
            if (data != null && data.scheme == "dewit" && data.host == "quickadd") {
                return data.getQueryParameter("text")
            }
        }
        if (Intent.ACTION_SEND == intent.action && intent.type == "text/plain") {
            (intent.getStringExtra(Intent.EXTRA_TEXT) ?: intent.getStringExtra(Intent.EXTRA_TITLE))?.let {
                if (it.isNotBlank()) return it
            }
        }
        return null
    }
}

