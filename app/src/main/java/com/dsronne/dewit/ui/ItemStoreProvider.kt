package com.dsronne.dewit.ui

import com.dsronne.dewit.storage.ItemStore

interface ItemStoreProvider {
    fun itemStore(): ItemStore
}

