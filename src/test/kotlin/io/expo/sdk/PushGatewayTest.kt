package io.expo.sdk

import io.expo.sdk.model.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockserver.client.server.MockServerClient
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import java.util.*

internal class PushGatewayTest {

    private val port = randomFrom()
    var mockServer: MockServerClient = MockServerClient("localhost", port)
    val url = "http://localhost:$port"


    @BeforeEach
    fun prepare() {
        mockServer = ClientAndServer.startClientAndServer(port)
    }
    @AfterEach
    fun tearDown() {
        mockServer.close()
    }

    @org.junit.jupiter.api.Test
    fun `test Android push notification`() {

        preparePushEndpoint()

        PushGateway(expoPushEndpointUrl = "$url/push")
                .push(
                        pushMessages = arrayListOf(PushMessage(
                                to = "expo-client-token",
                                data = Pair("test", "test-data"),
                                title =  "test-title",
                                body =  "The body of the message",
                                ttl = 10000,
                                expiration =  1000000,
                                priority = Priority.HIGH,
                                channelId = "test-channel"
                        )),
                        onSuccess = assertPushResponse()
                )
    }

    @org.junit.jupiter.api.Test
    fun `test IOS push notification`() {

        preparePushEndpoint()

        PushGateway(expoPushEndpointUrl = "$url/push")
                .push(
                        pushMessages = arrayListOf(PushMessage(
                                to = "expo-client-token",
                                data = Pair("test", "test-data"),
                                title =  "test-title",
                                body =  "The body of the message",
                                ttl = 10000,
                                expiration =  1000000,
                                priority = Priority.HIGH,
                                badge = 10,
                                sound = "boing"
                        )),
                        onSuccess = assertPushResponse()
                )
    }

    @org.junit.jupiter.api.Test
    fun `test http 400 bad request on push notification`() {

        prepareErrorEndpoint(400, "/push")

        PushGateway(expoPushEndpointUrl = "$url/push")
                .push(
                        pushMessages = arrayListOf(PushMessage(
                                to = "expo-client-token",
                                data = Pair("test", "test-data"),
                                title =  "test-title",
                                body =  "The body of the message",
                                ttl = 10000,
                                expiration =  1000000,
                                priority = Priority.HIGH,
                                badge = 10,
                                sound = "boing"
                        )),
                        onError = assert400Error()
                )
    }



    @org.junit.jupiter.api.Test
    fun `test http 500 error request on push notification`() {

        prepareErrorEndpoint(500, "/push")

        PushGateway(expoPushEndpointUrl = "$url/push")
                .push(
                        pushMessages = arrayListOf(PushMessage(
                                to = "expo-client-token",
                                data = Pair("test", "test-data"),
                                title =  "test-title",
                                body =  "The body of the message",
                                ttl = 10000,
                                expiration =  1000000,
                                priority = Priority.HIGH,
                                badge = 10,
                                sound = "boing"
                        )),
                        onError = assert500error()
                )
    }

    @org.junit.jupiter.api.Test
    fun `test receipt response when array of ids is passed as argument`() {
        prepareReceiptsEndpoint()

        PushGateway(expoReceiptsEndpointUrl = "$url/receipts")
                .receipts(listOf("id1", "id2"),
                        onSuccess = assertReceiptResponse()
                )
    }

    @org.junit.jupiter.api.Test
    fun `test receipt response when PushResponse instance is passed as argument`() {
        prepareReceiptsEndpoint()

        val pushResponse = PushResponse(arrayListOf(
                ResponseItem(status = ResponseStatus.OK, id = "12345"),
                ResponseItem(status = ResponseStatus.OK, id = "67890"),
                ResponseItem(status = ResponseStatus.ERROR, message = "some error")
        ))

        PushGateway(expoReceiptsEndpointUrl = "$url/receipts")
                .receipts(pushResponse,
                        onSuccess = assertReceiptResponse()
                )
    }

    @org.junit.jupiter.api.Test
    fun `test http 400 bad request on receipts notification`() {

        prepareErrorEndpoint(400, "/receipts")

        PushGateway(expoReceiptsEndpointUrl = "$url/receipts")
                .receipts(listOf("id1", "id2"),
                        onError = assert400Error()
                )
    }



    @org.junit.jupiter.api.Test
    fun `test http 500 error request on receipts notification`() {

        prepareErrorEndpoint(500, "/receipts")

        PushGateway(expoReceiptsEndpointUrl = "$url/receipts")
                .receipts(listOf("id1", "id2"),
                        onError = assert500error()
                )
    }

    @org.junit.jupiter.api.Test
    fun `test when push exception is thrown`() {

        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/receipts")
        )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withHeader("Content-Type", "application-json")
                                .withBody("Force parse error")
                )

        assertThrows(Exception::class.java) {
            PushGateway(expoReceiptsEndpointUrl = "$url/receipts")
                    .receipts(listOf("id1", "id2"))
        }
    }

    @org.junit.jupiter.api.Test
    fun `test when unknown response payload is returned`() {

        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/receipts")
        )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(300)
                                .withHeader("Content-Type", "application-json")
                                .withBody("This is unknown status and payload")
                )

            PushGateway(expoReceiptsEndpointUrl = "$url/receipts")
                    .receipts(
                            listOf("id1", "id2"),
                            onError = {
                                assertEquals(ResponseStatus.ERROR, it.status)
                                with(it.errors.first()){
                                    assertEquals("UNKNOWN", code)
                                    assertEquals("This is unknown status and payload", message)
                                }
                            }
                    )
    }

    private fun assertPushResponse(): (PushResponse) -> Unit {
        return { response ->
            println(response)
            assertNotNull(response)
            with(response) {
                assertNotNull(data)
                data.elementAt(0).let {
                    assertEquals(ResponseStatus.ERROR, it.status)
                    assertEquals("\"ExponentPushToken[xxxxxxxxxxxxxxxxxxxxxx]\" is not a registered push notification recipient", it.message)
                    assertEquals(ErrorDetail.DEVICE_NOT_REGISTRED, it.details?.error)
                }
                data.elementAt(1).let {
                    assertEquals(ResponseStatus.OK, it.status)
                    assertEquals("XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX", it.id)
                }
            }
        }
    }

    fun readFile(path: String): String = this.javaClass::class.java.getResource(path).readText()

    private fun preparePushEndpoint() {
        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/push")
        )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withHeader("Content-Type", "application-json")
                                .withBody(readFile("/responses/push_mixed_response.json"))
                )
    }

    private fun prepareReceiptsEndpoint() {
        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath("/receipts")
        )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(200)
                                .withHeader("Content-Type", "application-json")
                                .withBody(readFile("/responses/receipts_mixed_response.json"))
                )
    }

    private fun prepareErrorEndpoint(errorCode: Int, path: String) {
        mockServer.`when`(
                HttpRequest.request()
                        .withMethod("POST")
                        .withPath(path)
        )
                .respond(
                        HttpResponse.response()
                                .withStatusCode(errorCode)
                                .withHeader("Content-Type", "application-json")
                                .withBody(readFile("/responses/http_${errorCode}_error.json"))
                )
    }

    private fun assertReceiptResponse(): (ReceiptResponse) -> Unit {
        return { response ->
            println(response)
            assertNotNull(response)
            with(response) {
                assertNotNull(data)
                data["XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX"]?.let {
                    assertEquals(ResponseStatus.ERROR, it.status)
                    assertEquals("The Apple Push Notification service failed to send the notification", it.message)
                    assertEquals(ErrorDetail.DEVICE_NOT_REGISTRED, it.details?.error)
                }
                assertNotNull(data)
                data["YYYYYYYY-YYYY-YYYY-YYYY-YYYYYYYYYYYY"]?.let {
                    assertEquals(ResponseStatus.OK, it.status)
                }
            }
        }
    }

    private fun assert400Error(): (ErrorResponse) -> Unit {
        return { response ->
            println(response)
            assertNotNull(response)
            assertNotNull(response.errors)
            assertEquals(ResponseStatus.ERROR, response.status)
            assertEquals("NOT_FOUND", response.errors.first().code)
            assertEquals("Not found.", response.errors.first().message)
        }
    }

    private fun assert500error(): (ErrorResponse) -> Unit {
        return { response ->
            println(response)
            assertNotNull(response)
            assertNotNull(response.errors)
            assertEquals(ResponseStatus.ERROR, response.status)
            assertEquals("INTERNAL_SERVER_ERROR", response.errors.first().code)
            assertEquals("An unknown error occurred.", response.errors.first().message)
        }
    }
}


val random = Random()
internal fun randomFrom(from: Int = 1024, to: Int = 65535) : Int {
    return random.nextInt(to - from) + from
}

