package com.akylas.volumescroll.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Generic registerReceiver extension to reduce boilerplate
 *
 * Call this like so:
 * val myReceiver = registerReceiver(IntentFilter(BROADCAST_SOMETHING_HAPPENED)) {
 *     when (intent?.action) {
 *         BROADCAST_SOMETHING_HAPPENED -> handleSomethingHappened()
 *     }
 * }
 *
 * Call this extension from your Activity's onStart(), keep a reference
 * to the returned receiver and unregister it in onStop()
 *
 * Note: If you support devices on Honeycomb or earlier,
 * then you must call this in onResume() and unregister in onPause()
 */
fun Context.registerReceiver(intentFilter: IntentFilter, onReceive: (intent: Intent?) -> Unit): BroadcastReceiver {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            onReceive(intent)
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        this.registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED)
    } else {
        @Suppress("UnspecifiedRegisterReceiverFlag")
        this.registerReceiver(receiver, intentFilter)
    }

    return receiver
}