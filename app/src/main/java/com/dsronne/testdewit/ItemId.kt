package com.dsronne.testdewit

import java.util.UUID

data class ItemId(val id: String = UUID.randomUUID().toString())