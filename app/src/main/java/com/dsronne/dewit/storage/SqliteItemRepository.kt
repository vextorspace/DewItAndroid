package com.dsronne.dewit.storage

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.core.database.sqlite.transaction
import com.dsronne.dewit.datamodel.ItemId
import com.dsronne.dewit.datamodel.Item
import com.dsronne.dewit.datamodel.ListItem
import com.dsronne.dewit.datamodel.CopyWorkflow
import com.dsronne.dewit.datamodel.MoveWorkflow
import com.dsronne.dewit.datamodel.TextItem
import com.dsronne.dewit.domain.ports.ItemRepository

class SqliteItemRepository(context: Context) : ItemRepository {
    private val dbHelper = ItemDatabaseHelper(context)

    override fun save(item: ListItem) {
        val db = dbHelper.writableDatabase
        db.transaction {
            val values = ContentValues().apply {
                put(ItemDatabaseHelper.COL_ID, item.id.id)
                put(ItemDatabaseHelper.COL_LABEL, item.label())
                val data = item.data
                when (data) {
                    is TextItem -> put(ItemDatabaseHelper.COL_CONTENT, data.content)
                    else -> putNull(ItemDatabaseHelper.COL_CONTENT)
                }
            }
            insertWithOnConflict(
                ItemDatabaseHelper.TABLE_ITEMS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
            )
            delete(
                ItemDatabaseHelper.TABLE_CHILDREN,
                "${ItemDatabaseHelper.COL_PARENT_ID} = ?",
                arrayOf(item.id.id)
            )
            item.children.forEachIndexed { index, childId ->
                val cv = ContentValues().apply {
                    put(ItemDatabaseHelper.COL_PARENT_ID, item.id.id)
                    put(ItemDatabaseHelper.COL_CHILD_ID, childId.id)
                    put(ItemDatabaseHelper.COL_POSITION, index)
                }
                insertWithOnConflict(
                    ItemDatabaseHelper.TABLE_CHILDREN,
                    null,
                    cv,
                    SQLiteDatabase.CONFLICT_IGNORE
                )
            }
            // persist workflows for this item
            delete(
                ItemDatabaseHelper.TABLE_WORKFLOWS,
                "${ItemDatabaseHelper.COL_WORKFLOW_ITEM_ID} = ?",
                arrayOf(item.id.id)
            )
            item.workflows.forEach { workflow ->
                val cvw = ContentValues().apply {
                    put(ItemDatabaseHelper.COL_WORKFLOW_ITEM_ID, item.id.id)
                    when (workflow) {
                        is MoveWorkflow -> {
                            put(ItemDatabaseHelper.COL_WORKFLOW_TYPE, "move")
                            put(ItemDatabaseHelper.COL_WORKFLOW_TARGET_ID, workflow.targetId.id)
                        }
                        is CopyWorkflow -> {
                            put(ItemDatabaseHelper.COL_WORKFLOW_TYPE, "copy")
                            put(ItemDatabaseHelper.COL_WORKFLOW_TARGET_ID, workflow.targetId.id)
                        }
                        else -> return@forEach
                    }
                }
                insertWithOnConflict(
                    ItemDatabaseHelper.TABLE_WORKFLOWS,
                    null,
                    cvw,
                    SQLiteDatabase.CONFLICT_IGNORE
                )
            }
        }
    }

    override fun find(id: ItemId): ListItem? {
        val db = dbHelper.readableDatabase
        db.rawQuery(
            "SELECT ${ItemDatabaseHelper.COL_LABEL}, ${ItemDatabaseHelper.COL_CONTENT} FROM ${ItemDatabaseHelper.TABLE_ITEMS} WHERE ${ItemDatabaseHelper.COL_ID} = ?",
            arrayOf(id.id)
        ).use { cursor ->
            if (!cursor.moveToFirst()) return null
            val label = cursor.getString(cursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_LABEL))
            val contentIndex = cursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_CONTENT)
            val baseItem = if (!cursor.isNull(contentIndex)) {
                val content = cursor.getString(contentIndex)
                TextItem(label, content, ItemId(id.id))
            } else {
                Item(label, ItemId(id.id))
            }
            val item = ListItem(baseItem)
            db.rawQuery(
                "SELECT ${ItemDatabaseHelper.COL_CHILD_ID} FROM ${ItemDatabaseHelper.TABLE_CHILDREN} " +
                        "WHERE ${ItemDatabaseHelper.COL_PARENT_ID} = ? ORDER BY ${ItemDatabaseHelper.COL_POSITION} ASC",
                arrayOf(id.id)
            ).use { childCursor ->
                while (childCursor.moveToNext()) {
                    val cid = childCursor.getString(
                        childCursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_CHILD_ID)
                    )
                    item.children.add(ItemId(cid))
                }
            }
            // load workflows for this item
            db.rawQuery(
                "SELECT ${ItemDatabaseHelper.COL_WORKFLOW_TYPE}, ${ItemDatabaseHelper.COL_WORKFLOW_TARGET_ID} " +
                        "FROM ${ItemDatabaseHelper.TABLE_WORKFLOWS} WHERE ${ItemDatabaseHelper.COL_WORKFLOW_ITEM_ID} = ?",
                arrayOf(id.id)
            ).use { wfCursor ->
                while (wfCursor.moveToNext()) {
                    val type = wfCursor.getString(
                        wfCursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_WORKFLOW_TYPE)
                    )
                    val target = wfCursor.getString(
                        wfCursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_WORKFLOW_TARGET_ID)
                    )
                    when (type) {
                        "copy" -> item.addWorkflow(CopyWorkflow(ItemId(target)))
                        "move" -> item.addWorkflow(MoveWorkflow(ItemId(target)))
                        else -> {
                        }
                    }
                }
            }
            return item
        }
    }

    override fun findAll(): List<ListItem> {
        val db = dbHelper.readableDatabase
        val items = mutableListOf<ListItem>()
        db.rawQuery(
            "SELECT ${ItemDatabaseHelper.COL_ID}, ${ItemDatabaseHelper.COL_LABEL}, ${ItemDatabaseHelper.COL_CONTENT} FROM ${ItemDatabaseHelper.TABLE_ITEMS}",
            null
        ).use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_ID)
            val labelIndex = cursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_LABEL)
            val contentIndex = cursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_CONTENT)
            while (cursor.moveToNext()) {
                val id = cursor.getString(idIndex)
                val label = cursor.getString(labelIndex)
                val baseItem = if (!cursor.isNull(contentIndex)) {
                    val content = cursor.getString(contentIndex)
                    TextItem(label, content, ItemId(id))
                } else {
                    Item(label, ItemId(id))
                }
                items.add(ListItem(baseItem))
            }
        }
        items.forEach { item ->
            db.rawQuery(
                "SELECT ${ItemDatabaseHelper.COL_CHILD_ID} FROM ${ItemDatabaseHelper.TABLE_CHILDREN} " +
                        "WHERE ${ItemDatabaseHelper.COL_PARENT_ID} = ? ORDER BY ${ItemDatabaseHelper.COL_POSITION} ASC",
                arrayOf(item.id.id)
            ).use { childCursor ->
                while (childCursor.moveToNext()) {
                    val cid = childCursor.getString(
                        childCursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_CHILD_ID)
                    )
                    item.children.add(ItemId(cid))
                }
            }
            // load workflows for this item
            db.rawQuery(
                "SELECT ${ItemDatabaseHelper.COL_WORKFLOW_TYPE}, ${ItemDatabaseHelper.COL_WORKFLOW_TARGET_ID} " +
                        "FROM ${ItemDatabaseHelper.TABLE_WORKFLOWS} WHERE ${ItemDatabaseHelper.COL_WORKFLOW_ITEM_ID} = ?",
                arrayOf(item.id.id)
            ).use { wfCursor ->
                while (wfCursor.moveToNext()) {
                    val type = wfCursor.getString(
                        wfCursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_WORKFLOW_TYPE)
                    )
                    val target = wfCursor.getString(
                        wfCursor.getColumnIndexOrThrow(ItemDatabaseHelper.COL_WORKFLOW_TARGET_ID)
                    )
                    when (type) {
                        "copy" -> item.addWorkflow(CopyWorkflow(ItemId(target)))
                        "move" -> item.addWorkflow(MoveWorkflow(ItemId(target)))
                        else -> {
                        }
                    }
                }
            }
        }
        return items
    }

    override fun update(item: ListItem) {
        save(item)
    }

}
