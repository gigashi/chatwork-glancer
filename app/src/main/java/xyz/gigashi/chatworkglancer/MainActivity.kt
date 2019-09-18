package xyz.gigashi.chatworkglancer

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tokenEditText.setText(
            defaultSharedPreferences.token,
            TextView.BufferType.NORMAL
        )

        setTokenButton.setOnClickListener { GlobalScope.launch(Dispatchers.Main) {
            val token = tokenEditText.text.toString()
            withContext(Dispatchers.Default) { ChatworkApi.me(token) }.fold(
                success = { json ->
                    resultTextView.text = json.obj().toString(4)

                    defaultSharedPreferences.token = token
                    d("saved token: $token")

                    Toast.makeText(this@MainActivity, "成功", Toast.LENGTH_LONG).show()
                },
                failure = { error ->
                    resultTextView.text = error.toString()

                    Toast.makeText(this@MainActivity, "失敗", Toast.LENGTH_LONG).show()
                }
            )
        } }

        // 他アプリの通知を見る権限を確認
        if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)) {
            AlertDialog.Builder(this)
                .setTitle("need permission")
                .setMessage("allow access to notifications")
                .setPositiveButton("ok"){ _, _ ->
                    Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                        startActivity(this)
                    }
                }.show()
        }
    }
}
