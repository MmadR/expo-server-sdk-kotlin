package io.expo.sdk

class PushException(val endpoint: String,
                    val payload: String,
                    message: String? = null,
                    cause: Throwable? = null) : RuntimeException(message, cause)