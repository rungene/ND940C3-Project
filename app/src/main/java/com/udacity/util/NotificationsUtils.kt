package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

//  Step 1.1 extension function to send messages
/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(fileName: String, applicationContext: Context,status: String) {
    // Create the content intent for the notification, which launches
    // this activity
    //  Step 1.11 create intent
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
            .putExtra("fileName",fileName)
            .putExtra("status",status)

    // Step 1.12 create PendingIntent
    val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

    //  Step 2.0 add style
    val cloudImage  = BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.ic_notification_200dp
    )
    val bigPicStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(cloudImage)
            .bigLargeIcon(null)


    // Step 1.2 get an instance of NotificationCompat.Builder
    // Build the notification
    /*   To support devices running older versions you need to use NotificationCompat
       builder instead
       of notification builder*/
    val builder = NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.download_channel_id)
    )



            // Step 1.3 set title, text and icon to builder
            /* Set the notification icon to represent your app, title and the content text for
                 the message you want to give to the user.*/
            .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
            .setContentTitle(applicationContext
                    .getString(R.string.notification_title))
            .setContentText(fileName)

            //  Step 1.13 set content intent
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)



            //  Step 2.3 add action to open the details activity
            .addAction(0,
                    applicationContext.getString(R.string.notification_action_details),
                    contentPendingIntent)

            //  Step 2.5 set priority
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            // add style to builder
            .setStyle(bigPicStyle)
            .setLargeIcon(cloudImage)

    //  Step 1.4 call notify
    /*  call notify() with a unique id for your notification and with the Notification object from your
              builder. This id represents the current notification instance and is needed for updating
      or canceling this notification. Since your app will only have one active notification at a given
      time, you can use the same id for all your notifications. You are already given a constant for
          this purpose called NOTIFICATION_ID in NotificationUtils.kt. Notice we can directly call
                  notify() since you are performing the call from an extension function on the same
      class*/

    notify(NOTIFICATION_ID, builder.build())

}

//  Step 1.14 Cancel all notifications
/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
