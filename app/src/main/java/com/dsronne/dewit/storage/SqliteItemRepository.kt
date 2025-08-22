package com.dsronne.dewit.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.domain.ports.ItemRepository

class SqliteItemRepository(context: Context) : ItemRepository {
    private val dbHelper = ItemDatabaseHelper(context)

    override fun save(item: ListItem) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val values = ContentValues().apply {
                put(ItemDatabaseHelper.COL_ID, item.id.id)
                put(ItemDatabaseHelper.COL_LABEL, item.label())
            }
            db.insertWithOnConflict(
                ItemDatabaseHelper.TABLE_ITEMS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
            )
            db.delete(
                ItemDatabaseHelper.TABLE_CHILDREN,
                "${ItemDatabaseHelper.COL_PARENT_ID} = ?",
                arrayOf(item.id.id)
            )
            item.children.forEach { childId ->
                val cv = ContentValues().apply {
                    put(ItemDatabaseHelper.COL_PARENT_ID, item.id.id)
                    put(ItemDatabaseHelper.COL_CHILD_ID, childId.id)
                }
                db.insertWithOnConflict(
                    ItemDatabaseHelper.TABLE_CHILDREN,
                    null,
                    cv,
                    SQLiteDatabase.CONFLICT_IGNORE
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    override fun find(id: ItemId): ListItem? {
        val db = dbHelper.readableDatabase
        db.rawQuery(
            "SELECT ${ItemDatabaseHelper.COL_LABEL} FROM ${ItemDatabaseHelper.TABLE_ITEMS} WHERE ${ItemDatabaseHelper.COL_ID} = ?",
            arrayOf(id.id)
        ).use { cursor ->
            if (!cursor.moveToFirst()) return null
            val label = cursor.getString(cursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_LABEL))
            val item = ListItem(Item(label, ItemId(id.id)))
            db.rawQuery(
                "SELECT ${ItemDatabaseHelper.COL_CHILD_ID} FROM ${ItemDatabaseHelper.TABLE_CHILDREN} WHERE ${ItemDatabaseHelper.COL_PARENT_ID} = ?",
                arrayOf(id.id)
            ).use { childCursor ->
                while (childCursor.moveToNext()) {
                    val cid = childCursor.getString(
                        childCursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_CHILD_ID)
                    )
                    item.children.add(ItemId(cid))
                }
            }
            return item
        }
    }

    override fun findAll(): List<ListItem> {
        val db = dbHelper.readableDatabase
        val items = mutableListOf<ListItem>()
        db.rawQuery(
            "SELECT ${ItemDatabaseHelper.COL_ID}, ${ItemDatabaseHelper.COL_LABEL} FROM ${ItemDatabaseHelper.TABLE_ITEMS}",
            null
        ).use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_ID)
            val labelIndex = cursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_LABEL)
            while (cursor.moveToNext()) {
                val id = cursor.getString(idIndex)
                val label = cursor.getString(labelIndex)
                items.add(ListItem(Item(label, ItemId(id))))
            }
        }
        items.forEach { item ->
            db.rawQuery(
                "SELECT ${ItemDatabaseHelper.COL_CHILD_ID} FROM ${ItemDatabaseHelper.TABLE_CHILDREN} WHERE ${ItemDatabaseHelper.COL_PARENT_ID} = ?",
                arrayOf(item.id.id)
            ).use { childCursor ->
                while (childCursor.moveToNext()) {
                    val cid = childCursor.getString(
                        childCursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_CHILD_ID)
                    )
                    item.children.add(ItemId(cid))
                }
            }
        }
        return items
    }

    override fun update(item: ListItem) {
        save(item)
    }
}
