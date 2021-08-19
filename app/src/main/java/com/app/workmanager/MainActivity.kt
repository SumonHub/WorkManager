package com.app.workmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private val CHANNEL_ID = "CHANNEL_ID"
    private val GROUP_KEY_TEST = "GROUP_KEY_TEST"
    private val SUMMARY_ID = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChanel()

        findViewById<Button>(R.id.btnShowNotification).setOnClickListener {
            //showNotification()
            val syncWorker =
                PeriodicWorkRequestBuilder<SyncWorker>(16, TimeUnit.MINUTES)
                    .build()
            val syncWorkManager = WorkManager.getInstance(this)
            syncWorkManager.enqueueUniquePeriodicWork(
                "sendLogs",
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorker
            )

            syncWorkManager.getWorkInfosForUniqueWorkLiveData("sendLogs")
                .observe(this) { workInfo ->
                    Log.d(TAG, "onCreate: " + workInfo[0].state)
                    if (workInfo[0].state == WorkInfo.State.SUCCEEDED) {
                        showNotification()
                    }
                }

            /*val uploadWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<SyncWorker>()
                    // Additional configuration
                    .build()

            val syncWorkManager = WorkManager.getInstance(this)
            syncWorkManager.enqueue(uploadWorkRequest)
            syncWorkManager.getWorkInfoByIdLiveData(uploadWorkRequest.id)
                .observe(this) { workInfo ->
                    Log.d(TAG, "onCreate: " + workInfo?.state)

                    if(workInfo?.state == WorkInfo.State.SUCCEEDED) {
                        showNotification()
                    }
                }*/
        }
    }

    private fun createNotificationChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, "name", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "descriptionText"
                }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        Log.d(TAG, "showNotification: calling...")

        val notificationId = System.currentTimeMillis().toInt()
        val title = "Get Notification $notificationId"
        val message = "On ${
            SimpleDateFormat(
                "dd MMM yy - hh:mm:ss a",
                Locale.getDefault()
            ).format(Date().time)
        }"

        val notification1 =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_attachment_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setGroup(GROUP_KEY_TEST)
                .build()

        val summaryNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_list_24)
            .setContentTitle("New messages")
            //set content text to support devices running API level < 24
            .setContentText("New messages")
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setSummaryText("from Sumon")
            )
            //specify which group this notification belongs to
            .setGroup(GROUP_KEY_TEST)
            //set this notification as the summary for the group
            .setGroupSummary(true)
            .build()

        with(NotificationManagerCompat.from(this)) {
            this.notify(notificationId, notification1)
            this.notify(SUMMARY_ID, summaryNotification)
        }
    }
}