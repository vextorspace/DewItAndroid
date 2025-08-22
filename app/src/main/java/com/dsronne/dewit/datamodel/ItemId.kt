package com.dsronne.dewit.datamodel

import java.util.UUID

data class ItemId(val id: String = UUID.randomUUID().toString())