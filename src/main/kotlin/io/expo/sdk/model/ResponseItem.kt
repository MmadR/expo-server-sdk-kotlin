package io.expo.sdk.model

data class ResponseItem(val status: ResponseStatus,
                        val id: String? = null,
                        val message: String? = null,
                        val details: Detail? = null
                        )

data class Detail(val error: ErrorDetail)

enum class ErrorDetail(val code: String) {
    /**
     * Device cannot receive push notifications anymore and you should stop sending messages to the corresponding Expo push token.
     */
    DEVICE_NOT_REGISTRED("DeviceNotRegistered"),
    /**
     * Total notification payload was too large. On Android and iOS the total payload must be at most 4096 bytes.
     */
    MESSAGE_TOO_BIG("MessageTooBig"),
    /**
     * Sending messages too frequently to the given device. Implement exponential backoff and slowly retry sending messages.
     */
    MESSAGE_RATE_EXCEEDED("MessageRateExceeded"),
    /**
     * Push notification credentials for your standalone app are invalid (ex: you may have revoked them). Run expo build:ios -c to regenerate new push notification credentials for iOS.
     */
    INVALID_CREDENTIALS("InvalidCredentials"),
    /**
     * If API returns error
     */
    UNKNOWN("UNKNOWN")
}

