package com.dsronne.dewit.datamodel

class TextItem(label: String, id: ItemId = ItemId(), var content: String = "") : Item(label, id) {
}