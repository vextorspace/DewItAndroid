package com.dsronne.dewit.datamodel

class TextItem(label: String, var content: String = "", id: ItemId = ItemId()) : Item(label, id) {
}