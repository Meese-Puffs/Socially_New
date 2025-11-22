package com.Ahmad_Kamran.i230622

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "SociallyOfflineDB"

        // Table for Queued Actions
        private const val TABLE_QUEUED_ACTIONS = "queued_actions"
        private const val KEY_ID = "id"
        private const val KEY_TYPE = "type"
        private const val KEY_PAYLOAD_JSON = "payload_json"
        private const val KEY_TIMESTAMP = "timestamp"
        private const val KEY_IS_SYNCED = "is_synced"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // SQL statement to create the Queued Actions table
        val CREATE_QUEUED_ACTIONS_TABLE = ("CREATE TABLE $TABLE_QUEUED_ACTIONS ("
                + "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_TYPE TEXT NOT NULL,"
                + "$KEY_PAYLOAD_JSON TEXT NOT NULL,"
                + "$KEY_TIMESTAMP INTEGER NOT NULL,"
                + "$KEY_IS_SYNCED INTEGER DEFAULT 0" // SQLite stores Boolean as INTEGER (0=false, 1=true)
                + ")")
        db.execSQL(CREATE_QUEUED_ACTIONS_TABLE)
        Log.d("DatabaseHelper", "Table created: $TABLE_QUEUED_ACTIONS")

        // TODO: Add table creation for local 'messages', 'posts', and 'stories' here
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Simple destructive upgrade strategy for this example
        db.execSQL("DROP TABLE IF EXISTS $TABLE_QUEUED_ACTIONS")
        onCreate(db)
    }

    /**
     * Adds a new action to the queue when the device is offline.
     */
    fun addQueuedAction(action: QueuedAction): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_TYPE, action.type)
            put(KEY_PAYLOAD_JSON, action.payloadJson)
            put(KEY_TIMESTAMP, action.timestamp)
            put(KEY_IS_SYNCED, if (action.isSynced) 1 else 0)
        }

        val id = db.insert(TABLE_QUEUED_ACTIONS, null, values)
        db.close()
        Log.i("DatabaseHelper", "Queued Action added with ID: $id. Type: ${action.type}")
        return id
    }

    /**
     * Retrieves all unsynced actions, ordered by timestamp (oldest first).
     */
    fun getUnsyncedActions(): List<QueuedAction> {
        val actionList = mutableListOf<QueuedAction>()
        val selectQuery = "SELECT * FROM $TABLE_QUEUED_ACTIONS WHERE $KEY_IS_SYNCED = 0 ORDER BY $KEY_TIMESTAMP ASC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        cursor.use { c ->
            if (c.moveToFirst()) {
                do {
                    val id = c.getLong(c.getColumnIndexOrThrow(KEY_ID))
                    val type = c.getString(c.getColumnIndexOrThrow(KEY_TYPE))
                    val payloadJson = c.getString(c.getColumnIndexOrThrow(KEY_PAYLOAD_JSON))
                    val timestamp = c.getLong(c.getColumnIndexOrThrow(KEY_TIMESTAMP))
                    val isSynced = c.getInt(c.getColumnIndexOrThrow(KEY_IS_SYNCED)) == 1

                    actionList.add(QueuedAction(id, type, payloadJson, timestamp, isSynced))
                } while (c.moveToNext())
            }
        }
        db.close()
        return actionList
    }

    /**
     * Marks a specific action as synced after successful server communication.
     */
    fun markActionAsSynced(id: Long): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_IS_SYNCED, 1)
        }
        val rowsAffected = db.update(TABLE_QUEUED_ACTIONS, values, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
        if (rowsAffected > 0) {
            Log.d("DatabaseHelper", "Action ID $id marked as synced.")
        }
        return rowsAffected
    }

    /**
     * Optionally, delete the synced action to keep the queue clean.
     */
    fun deleteSyncedAction(id: Long): Int {
        val db = this.writableDatabase
        val rowsDeleted = db.delete(TABLE_QUEUED_ACTIONS, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
        if (rowsDeleted > 0) {
            Log.d("DatabaseHelper", "Action ID $id deleted.")
        }
        return rowsDeleted
    }
}