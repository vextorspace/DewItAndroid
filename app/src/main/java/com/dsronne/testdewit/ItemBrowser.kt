package com.dsronne.testdewit

interface ItemBrowser {
    fun getChildrenOf(itemId: ItemId): List<ListItem>
    fun root(): ListItem
    fun find(id: ItemId): ListItem?
}
