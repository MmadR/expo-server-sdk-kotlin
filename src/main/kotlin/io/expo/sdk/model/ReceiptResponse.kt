package io.expo.sdk.model

data class ReceiptResponse(val data: Map<String, ResponseItem>): Response
{
    override val status = ResponseStatus.OK
}