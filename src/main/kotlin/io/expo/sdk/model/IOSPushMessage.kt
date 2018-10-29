package io.expo.sdk.model

/**
 * IOS push notification message
 */
data class IOSPushMessage(
        override val to: String,
        override val data: Any,
        override val title: String? = null,
        override val body: String? = null,
        override val ttl: Long? = null,
        override val expiration: Long? = null,
        override val priority: Priority = Priority.DEFAULT,
        /**
         * A sound to play when the recipient receives this notification. Specify
         * "default" to play the device's default notification sound, or omit this
         * field to play no sound.
         *
         * Note that on apps that target Android 8.0+ (if using `expo build`, built
         * in June 2018 or later), this setting will have no effect on Android.
         * Instead, use `channelId` and a channel with the desired setting.
         */
        val sound: String? = null,
        /**
         * Number to display in the badge on the app icon. Specify zero to clear the
         * badge.
         */
        val badge: Int? = null

): PushMessage