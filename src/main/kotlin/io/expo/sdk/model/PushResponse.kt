package io.expo.sdk.model

data class PushResponse(val data: Collection<ResponseItem>): Response
{
    override val status = ResponseStatus.OK
}