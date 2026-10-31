package com.aether.application.core.notification.data

import android.util.Log
import com.aether.application.core.notification.domain.DeviceTokenRegistrar
import com.aether.application.core.notification.domain.EXTRA_REPORT_ID
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FcmMessagingService: FirebaseMessagingService(), KoinComponent {
    private val deviceTokenRegistrar: DeviceTokenRegistrar by inject()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val presenter: NotificationPresenter by inject()

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM", "Message received: ${message.messageId}")
        Log.d("FCM", "Data: ${message.data}")

        val title = message.notification?.title ?: message.data["title"]
        val body = message.notification?.body ?: message.data["body"]
        presenter.show(title, body, message.data[EXTRA_REPORT_ID]?.toLongOrNull())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        scope.launch { deviceTokenRegistrar.onTokenRefreshed(token) }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
