package com.dsronne.dewit.ui

import com.dsronne.dewit.datamodel.ItemId

interface RootPagerController {
    fun onRootChildRemoved(removedId: ItemId)
    fun navigateToTopLevel(itemId: ItemId)
}
