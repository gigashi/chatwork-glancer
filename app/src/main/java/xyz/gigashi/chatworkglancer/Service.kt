package xyz.gigashi.chatworkglancer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject

class Service : NotificationListenerService() {

    override fun onNotificationPosted(notification: StatusBarNotification?) {
        super.onNotificationPosted(notification)

        // chatworkからの通知以外無視
        if(notification?.packageName != "jp.ecstudio.chatworkandroid") { return }

        // メッセージ以外無視
        if(notification.notification.channelId != "MESSAGE") { return }

        // token未登録なら処理しない
        val token = defaultSharedPreferences.token
        if(token == "") { return }

        GlobalScope.launch(Dispatchers.Main) {
            val rooms = withContext(Dispatchers.Default) { ChatworkApi.rooms(token) }
            val roomId = rooms.component1()?.array()?.objectList
                // 一番最初に未読が1以上あるルームを最新のメッセージがあるルームとみなす
                // (そんな感じの動作だった)
                ?.first { it.optInt("unread_num", 0) > 0 }
                ?.optInt("room_id", 0)
                ?.takeIf { it != 0 }
                ?: return@launch
            val messages = withContext(Dispatchers.Default) { ChatworkApi.messages(token, ""+roomId) }
            // 最後の要素を最新とみなす
            val message = messages.component1()?.array()?.objectList?.last() ?: return@launch
            val name = message.optJSONObject("account")?.optString("name", "unknown") ?: "unknown"
            val body = message.optString("body", "") ?: ""

            notify(name, body)
        }
    }

    private val JSONArray.objectList get() =
        (0 until length())
            .map { get(it) }
            .mapNotNull { it as? JSONObject }

    private fun notify(title: String, text: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager ?: return
        val name = "メッセージ"
        val id = "MESSAGE"

        if (notificationManager.getNotificationChannel(id) == null) {
            val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
            channel.enableVibration(true)
        }

        notificationManager.notify(
            uniqueId,
            NotificationCompat
                .Builder(this, id)
                .apply {
                    setSmallIcon(R.drawable.notification_icon)
                    setContentTitle(title)
                    setContentText(text)
                    setGroup("message")

                    packageManager.getLaunchIntentForPackage("jp.ecstudio.chatworkandroid")
                        ?.let { PendingIntent.getActivity(applicationContext, 0, it, PendingIntent.FLAG_ONE_SHOT) }
                        ?.let { setContentIntent(it) }
                }
                .build()
                .apply { flags += Notification.FLAG_AUTO_CANCEL }
        )
    }

    private val uniqueId get() = (System.currentTimeMillis().and(Int.MAX_VALUE.toLong()).toInt())
}