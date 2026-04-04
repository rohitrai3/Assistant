package dev.rohitrai.assistant.ui.screen

import android.app.Service
import android.content.Intent
import android.os.IBinder

class DuplexStreamForegroundService : Service() {
    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    companion object {
        val ACTION_START = "ACTION_START"
    }
}