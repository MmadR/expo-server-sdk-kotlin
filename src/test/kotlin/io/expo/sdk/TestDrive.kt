package io.expo.sdk

import io.expo.sdk.model.Priority
import io.expo.sdk.model.PushMessage

class TestDrive {

    @org.junit.jupiter.api.Test
    fun `test publishing a message to exponent channel using android settings`(){

        val gateway = PushGateway()

        gateway.push(
                pushMessages = arrayListOf(PushMessage(
                        to = listOf("ExponentPushToken[xxxxxxxxxxxxxxx]"),
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