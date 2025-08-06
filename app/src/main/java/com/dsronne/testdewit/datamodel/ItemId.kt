package com.dsronne.testdewit.datamodel

import java.util.UUID

data class ItemId(val id: String = UUID.randomUUID().toString())