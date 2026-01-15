package com.example.demo.exception

sealed interface ErrorCode {
    val name: String
    val message: String
}