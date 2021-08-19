package com.app.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class SyncWorker(context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams) {
    val TAG = "SyncWorker"
    override fun doWork(): Result {
        // Do the work here--in this case, upload the images.
        //runSync(context)
        // Indicate whether the work finished successfully with the Result

        Log.d(TAG, "doWork: calling...")
        return Result.success()
    }
}
