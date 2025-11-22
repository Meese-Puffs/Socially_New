package com.Ahmad_Kamran.i230622


data class QueuedAction(
    val id: Long = 0,               // Primary key in SQLite
    val type: String,               // e.g., "SEND_MESSAGE", "CREATE_POST", "LIKE_POST"
    val payloadJson: String,        // JSON string containing all necessary data for the server (e.g., recipient_id, message_text)
    val timestamp: Long,            // When the action was queued (for ordering retries)
    val isSynced: Boolean = false   // Flag to indicate successful server sync
) {
    companion object {
        const val TYPE_SEND_MESSAGE = "SEND_MESSAGE"
        const val TYPE_CREATE_POST = "CREATE_POST"
        const val TYPE_LIKE_POST = "LIKE_POST"
        // Add other action types as needed
    }
}