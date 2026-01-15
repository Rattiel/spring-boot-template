package com.example.demo.exception

enum class GlobalErrorCode(
    override val message: String,
) : ErrorCode {
    UNKNOWN_ERROR("알 수 없는 오류가 발생하였습니다."),
}