package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import androidx.lifecycle.Observer


class MainActivity : AppCompatActivity() {
    private lateinit var customButton: LoadingButton

    private var downloadID: Long = 0

    private var downloadURL: String? = null
    private lateinit var fileName: String

    private lateinit var notificationManager: NotificationManager

    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        customButton = findViewById(R.id.custom_button)
        custom_button.setOnClickListener {
            download()
        }

        //   create channel for download notification
        createChannel(
                getString(R.string.download_channel_id),
                getString(R.string.download_channel_name)
        )

         // Initialize the notification manager
        // NotificationManager is a system service which provides all the functions exposed
    // for notifications api
        notificationManager =ContextCompat.getSystemService(
                applicationContext,
                NotificationManager::class.java
        ) as NotificationManager


    }

    fun onRadioButtonClicked(view: View) {
        if(view is RadioButton){
            //check if the radio button is checked
            val checked = view.isChecked

            //check which radio button was checked
            customButton.buttonState = ButtonState.Clicked
            when(view.id){
                R.id.glide_button->
                    if (checked){
                        fileName = getString(R.string.glide_button_text)
                        downloadURL = URL_GLIDE
                    }

                R.id.loadapp_button->
                    if (checked){
                        fileName = getString(R.string.loadapp_button_text)
                        downloadURL = URL_LOAD_APP
                    }
                R.id.retrofit_button->
                    if (checked){
                        fileName = getString(R.string.retrofit_button_text)
                        downloadURL = URL_RETROFIT
                    }



            }

        }


    }



    private fun createChannel(channelId: String, channelName: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        //  Step 1.6 START create a channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    //  Step 2.4 change importance
                    NotificationManager.IMPORTANCE_HIGH
            )//  Step 2.6 disable badges for this channel
                    .apply {
                        setShowBadge(false)
                    }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.download_channel_description)

            // Register the channel with the system


            val notificationManager = this.getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
        //  Step 1.6 END create a channel
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id ==downloadID){
                //reset download button state and stop the animation
                customButton.buttonState =ButtonState.Completed
                notificationManager.sendNotification(downloadURL.toString(),applicationContext,"Success")


            }else{
                customButton.buttonState =ButtonState.Completed
                notificationManager.sendNotification(downloadURL.toString(),applicationContext,"Failed")
            }


        }
    }

    private fun download() {

        if (downloadURL ==null){
            Toast.makeText(this,"Please select an item",Toast.LENGTH_LONG).show()
        }else{
            customButton.buttonState =ButtonState.Loading

            val request =
                DownloadManager.Request(Uri.parse(downloadURL))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.

            val processingLoadingData = ProcessingLoadingData(application, downloadID)
            processingLoadingData.observe(this, Observer {
                var floatProgress = ((it.loadedInTimeBt * 100L) / it.sizeOfFileInBt).toFloat()
                if (it.sizeOfFileInBt != (-1).toLong()){
                    floatProgress = (floatProgress / 100)
                    custom_button.settingProgressOfButton(floatProgress)
                }else if (it.loadedInTimeBt > 1) {
                    custom_button.addingButtonProgress(0.05f)
                }

            })
        }



    }

    companion object {
        private const val URL_GLIDE = "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_LOAD_APP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_RETROFIT = "https://github.com/square/retrofit/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }




}
