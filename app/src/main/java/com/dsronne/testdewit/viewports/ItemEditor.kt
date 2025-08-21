package com.dsronne.dewit.viewports

import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ListItem

interface ItemEditor {

    fun add(newItem: ListItem)

    fun edit(changedItem: ListItem)
}