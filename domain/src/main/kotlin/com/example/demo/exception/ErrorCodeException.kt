package com.example.demo.exception

class ErrorCodeException(
    val errorCode: ErrorCode,
    val args: Array<out Any>? = null,
) : RuntimeException(errorCode.message)