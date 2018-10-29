package io.expo.sdk

import io.expo.sdk.model.*
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class PushGateway(
        private val expoPushEndpointUrl: String = PushGateway.EXPO_PUSH_ENDPOINT,
        private val expoReceiptsEndpointUrl: String = PushGateway.EXPO_RECEIPT_ENDPOINT
)
{
    companion object {
        const val EXPO_PUSH_ENDPOINT: String = "https://exp.host/--/api/v2/push/send"
        const val EXPO_RECEIPT_ENDPOINT: String = "https://exp.host/--/api/v2/push/getReceipts"
        private val jsonSerializer: KlaxonJsonSerializer = KlaxonJsonSerializer()
    }

    fun push(pushMessages: Collection<PushMessage>,
             onSuccess: (PushResponse) -> Unit = {},
             onError: (ErrorResponse) -> Unit = {}) {
        request(expoPushEndpointUrl,
                jsonSerializer.toJson(pushMessages),
                {input -> jsonSerializer.fromJson<PushResponse>(input)},
                {input -> jsonSerializer.fromJson<ErrorResponse>(input)}
        ).let{
            when(it){
                is PushResponse -> onSuccess.invoke(it)
                else -> onError.invoke(it as ErrorResponse)
            }
        }
    }

    fun receipts(ids: Collection<String>,
                 onSuccess: (ReceiptResponse) -> Unit = {},
                 onError: (ErrorResponse) -> Unit = {}) {
        request(expoReceiptsEndpointUrl,
                jsonSerializer.toJson(mapOf(Pair("ids", ids))),
                {input -> jsonSerializer.fromJson<ReceiptResponse>(input)},
                {input -> jsonSerializer.fromJson<ErrorResponse>(input)}
        ).let{
            when(it){
                is ReceiptResponse -> onSuccess.invoke(it)
                else -> onError.invoke(it as ErrorResponse)
            }
        }
    }

    fun receipts(responseData: PushResponse,
                 onSuccess: (ReceiptResponse) -> Unit = {},
                 onError: (ErrorResponse) -> Unit = {}) {
            receipts(
                    responseData.data
                            .filter { ResponseStatus.OK == it.status }
                            .map{ it.id }
                            .filterNotNull(),
                    onSuccess,
                    onError
            )
    }


    private fun request(endpoint: String,
                        payload: String,
                        successCallback: (InputStream) -> Response?,
                        errorCallback: (InputStream) -> ErrorResponse?
    ): Response {
        try {
            val connection = URL(endpoint).openConnection() as HttpURLConnection
            connection.addRequestProperty("Accept", "application/json")
            connection.addRequestProperty("Accept-Encoding", "gzip, deflate")
            connection.addRequestProperty("Content-Type", "application/json")
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.outputStream.write(payload.toByteArray())
            connection.connect()

            return when(connection.responseCode ){
                200 -> successCallback.invoke(connection.inputStream)
                else -> {
                    (connection.errorStream?:connection.inputStream)?.let{inputStream ->
                        when(connection.responseCode){
                            in 400..599 -> errorCallback.invoke(inputStream)
                            else -> ErrorResponse(arrayListOf(ErrorResponseItem("UNKNOWN", inputStream.use { it.reader().use { reader -> reader.readText() } })))
                        }
                    }
                }
            }?: ErrorResponse(arrayListOf(ErrorResponseItem("PARSE_RESPONSE_ERROR", "Could not parse response")))
        }
        catch (ex: Exception){
            throw PushException(message =  "Error sending push notification",  cause = ex)
        }
    }
}

