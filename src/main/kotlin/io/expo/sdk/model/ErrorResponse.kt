package io.expo.sdk.model

data class ErrorResponse(val errors: Collection<ErrorResponseItem>): Response
{
    override val status = ResponseStatus.ERROR
}