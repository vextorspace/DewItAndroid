package com.dsronne.dewit.datamodel

import com.dsronne.dewit.storage.ItemStore

interface Workflow {
    fun name(): String
    fun apply(itemStore: ItemStore, parentId: ItemId, item: ListItem): Boolean
}