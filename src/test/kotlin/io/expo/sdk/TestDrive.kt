package io.expo.sdk

import io.expo.sdk.model.AndroidPushMessage
import io.expo.sdk.model.Priority

class TestDrive {

    @org.junit.jupiter.api.Test
    fun testAndroid(){

        val gateway = PushGateway()

        gateway.push(
                pushMessages = arrayListOf(AndroidPushMessage(
                        to = "ExponentPushToken[xxxxxxxxxxxxxxx]",
                        data = Pair("test", "test-data"),
                        title =  "test-title",
                        body =  "The body of the message",
                        ttl = 10000,
                        expiration =  1000000,
                        priority = Priority.HIGH,
                        channelId = "test-channel"
                )),
                onSuccess = {
                    println(it)
                    gateway.receipts(it, {println(it)}, {println(it)})
                }
        )
    }

}