package io.expo.sdk.model

data class AndroidPushMessage(
        override val to: String,
        override val data: Any,
        override val title: String? = null,
        override val body: String? = null,
        override val ttl: Long? = null,
        override val expiration: Long? = null,
        override val priority: Priority = Priority.DEFAULT,
        /**
         * ID of the Notification Channel through which to display this notification
         * on Android devices. If an ID is specified but the corresponding channel
         * does not exist on the device (i.e. has not yet been created by your app),
         * the notification will not be displayed to the user.
         *
         * If left null, a "Default" channel will be used, and Expo will create the
         * channel on the device if it does not yet exist. However, use caution, as
         * the "Default" channel is user-facing and you may not be able to fully
         * delete it.
         */
        val channelId: String? = null

): PushMessage