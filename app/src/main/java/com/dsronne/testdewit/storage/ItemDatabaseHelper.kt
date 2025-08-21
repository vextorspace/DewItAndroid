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
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CHILDREN")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ITEMS")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "items.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ITEMS = "items"
        const val COL_ID = "id"
        const val COL_LABEL = "label"

        const val TABLE_CHILDREN = "item_children"
        const val COL_PARENT_ID = "parent_id"
        const val COL_CHILD_ID = "child_id"
    }
}
