package com.xcaret.xcaret_hotel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.NotificationParams
import com.google.firebase.messaging.RemoteMessage
import com.salesforce.marketingcloud.messages.push.PushMessageManager
import com.salesforce.marketingcloud.sfmcsdk.SFMCSdk
import com.xcaret.xcaret_hotel.view.general.ui.MainActivity


class MyFirebaseMessagingService: FirebaseMessagingService(){

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        sendRegistrationToServer(token)
        SFMCSdk.requestSdk { sdk ->
            sdk.mp{
                it.pushMessageManager.setPushToken(token)
            }

        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: ${remoteMessage}")

        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        if(PushMessageManager.isMarketingCloudPush(remoteMessage)){
//            SFMCSdk.requestSdk { sdk ->
//                sdk.mp{
//                    it.pushMessageManager.handleMessage(remoteMessage)
//                }
//
//            }
            sendNotification(remoteMessage.data["alert"].toString(),remoteMessage.data["title"].toString(), remoteMessage.ttl)
        }else {
            remoteMessage.notification?.let {
                Log.d(TAG, "Message Notification body: ${it.body}")
                sendNotification(it.body.toString(),it.title.toString(), remoteMessage.ttl)
            }
        }
    }

    private fun sendNotification(body:String ="",title :String="", id: Int ){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) //FLAG_ACTIVITY_SINGLE_TOP
        val pendigIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT) //FLAG_UPDATE_CURRENT

        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendigIntent)

        //notification.defaultSound

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //Desde Android Oreo la notificacion por canal es necesaria
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel (channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(channelId, id, notificationBuilder.build())
    }

    private fun sendRegistrationToServer(token: String) {}

    companion object{
        private const val TAG = "MyFirebaseMggService"
    }

}