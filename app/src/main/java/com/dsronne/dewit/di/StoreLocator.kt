package com.dsronne.dewit.di

import android.content.Context
import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.storage.ItemStore
import com.dsronne.dewit.storage.SqliteItemRepository

object StoreLocator {
    @Volatile private var instance: ItemStore? = null

    fun itemStore(appContext: Context): ItemStore {
        return instance ?: synchronized(this) {
            instance ?: run {
                val repo = SqliteItemRepository(appContext)
                val store = ItemStore(repo)
                // Ensure root exists; if first run, initialize program management
                if (repo.find(ItemId("root")) == null) {
                    store.initProgramManagement()
                }
                store
            }.also { instance = it }
        }
    }
}

