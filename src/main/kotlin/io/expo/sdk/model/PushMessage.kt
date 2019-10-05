package io.expo.sdk.model

/**
 * Expo notification message data
 */
data class PushMessage(
        /**
         * An Expo push token specifying the recipient of this message.
         */
        val to: String,

        /**
         * A JSON object delivered to your app. It may be up to about 4KiB; the total
         * notification payload sent to Apple and Google must be at most 4KiB or else
         * you will get a "Message Too Big" error.
         */
        val data: Any,

        /**
         * The title to display in the notification. Devices often display this in
         * bold above the notification body. Only the title might be displayed on
         * devices with smaller screens like Apple Watch.
         */
        val title: String?,

        /**
         * The message to display in the notification
         */
        val body: String?,

        /**
         * Time to Live: the number of seconds for which the message may be kept
         * around for redelivery if it hasn't been delivered yet. Defaults to 0.
         *
         * On Android, we make a best effort to deliver messages with zero TTL
         * immediately and do not throttle them
         *
         * This field takes precedence over `expiration` when both are specified.
         */
        val ttl: Long?,

        /**
         * A timestamp since the UNIX epoch specifying when the message expires. This
         * has the same effect as the `ttl` field and is just an absolute timestamp
         * instead of a relative time.
         */
        val expiration: Long?,

        /**
         * The delivery priority of the message. Specify "default" or omit this field
         * to use the default priority on each platform, which is "normal" on Android
         * and "high" on iOS.
         *
         * On Android, normal-priority messages won't open network connections on
         * sleeping devices and their delivery may be delayed to conserve the battery.
         * High-priority messages are delivered immediately if possible and may wake
         * sleeping devices to open network connections, consuming energy.
         *
         * On iOS, normal-priority messages are sent at a time that takes into account
         * power considerations for the device, and may be grouped and delivered in
         * bursts. They are throttled and may not be delivered by Apple. High-priority
         * messages are sent immediately. Normal priority corresponds to APNs priority
         * level 5 and high priority to 10.
         */
        val priority: Priority,

        // Android only

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
        val channelId: String? = null,

        // IOS only

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
)





