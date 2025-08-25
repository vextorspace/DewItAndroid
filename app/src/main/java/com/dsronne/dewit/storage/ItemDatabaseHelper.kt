package com.dsronne.dewit.storage

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class ItemDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_ITEMS (" +
                    "$COL_ID TEXT PRIMARY KEY, " +
                    "$COL_LABEL TEXT NOT NULL" +
                    ")"
        )
        db.execSQL(
            "CREATE TABLE $TABLE_CHILDREN (" +
                    "$COL_PARENT_ID TEXT NOT NULL, " +
                    "$COL_CHILD_ID TEXT NOT NULL, " +
                    "PRIMARY KEY($COL_PARENT_ID, $COL_CHILD_ID), " +
                    "FOREIGN KEY($COL_PARENT_ID) REFERENCES $TABLE_ITEMS($COL_ID), " +
                    "FOREIGN KEY($COL_CHILD_ID) REFERENCES $TABLE_ITEMS($COL_ID)" +
                    ")"
        )
        db.execSQL(
            "CREATE TABLE $TABLE_WORKFLOWS (" +
                    "$COL_WORKFLOW_ITEM_ID TEXT NOT NULL, " +
                    "$COL_WORKFLOW_TYPE TEXT NOT NULL, " +
                    "$COL_WORKFLOW_TARGET_ID TEXT NOT NULL, " +
                    "PRIMARY KEY($COL_WORKFLOW_ITEM_ID, $COL_WORKFLOW_TYPE, $COL_WORKFLOW_TARGET_ID), " +
                    "FOREIGN KEY($COL_WORKFLOW_ITEM_ID) REFERENCES $TABLE_ITEMS($COL_ID)" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKFLOWS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CHILDREN")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "items.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_ITEMS = "items"
        const val COL_ID = "id"
        const val COL_LABEL = "label"

        const val TABLE_CHILDREN = "item_children"
        const val COL_PARENT_ID = "parent_id"
        const val COL_CHILD_ID = "child_id"
        const val TABLE_WORKFLOWS = "item_workflows"
        const val COL_WORKFLOW_ITEM_ID = "item_id"
        const val COL_WORKFLOW_TYPE = "workflow_type"
        const val COL_WORKFLOW_TARGET_ID = "target_id"
    }
}
