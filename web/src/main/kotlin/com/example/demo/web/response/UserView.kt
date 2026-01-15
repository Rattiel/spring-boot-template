package com.example.demo.web.response

import com.example.demo.model.User

interface UserView {
    val id: String
}

fun UserView(
    id: String,
): UserView = UserViewImpl(
    id = id,
)

private data class UserViewImpl(
    override val id: String,
) : UserView

fun User.toView(): UserView = UserView(
    id = id,
)