package com.dsronne.testdewit

class Path(private val segments: List<ItemId> = listOf(ItemId("root"))) {
    init {
        require(segments.isNotEmpty() && segments.first() == ItemId("root")) {
            "Path must start with 'root'"
        }
    }

    operator fun get(index: Int): ItemId = segments[index]

    fun size(): Int = segments.size

    companion object {
        fun root() = Path()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Path) return false
        return segments == other.segments
    }

    override fun hashCode(): Int {
        return segments.hashCode()
    }

    override fun toString(): String {
        return segments.joinToString("/")
    }
}