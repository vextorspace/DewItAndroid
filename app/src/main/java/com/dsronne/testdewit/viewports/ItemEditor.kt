package com.dsronne.testdewit.viewports

import com.dsronne.testdewit.datamodel.Item
import com.dsronne.testdewit.datamodel.ListItem

interface ItemEditor {

    fun add(newItem: ListItem)

    fun edit(changedItem: ListItem)
}