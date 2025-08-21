package com.dsronne.dewit.viewports

import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem

interface ItemBrowser {
    fun getChildrenOf(itemId: ItemId): List<ListItem>
    fun root(): ListItem
    fun find(id: ItemId): ListItem?
}