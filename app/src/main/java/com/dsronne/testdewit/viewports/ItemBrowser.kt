package com.dsronne.testdewit.viewports

import com.dsronne.testdewit.datamodel.ItemId
import com.dsronne.testdewit.datamodel.ListItem

interface ItemBrowser {
    fun getChildrenOf(itemId: ItemId): List<ListItem>
    fun root(): ListItem
    fun find(id: ItemId): ListItem?
}