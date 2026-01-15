package com.example.demo.web.test.support

import org.springframework.core.annotation.AliasFor
import org.springframework.security.test.context.support.WithSecurityContext
import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@WithSecurityContext(factory = WithJwtSecurityContextFactory::class)
annotation class WithJwt(
    val issuer: String = "https://issuer.example.com",

    @get:AliasFor("value")
    val subject: String = "mock-test-subject",

    @get:AliasFor("subject")
    val value: String = "mock-test-subject",

    val audience: Array<String> = ["https://audience.example.com"],

    val expiresAt: String = "",
    val notBefore: String = "",
    val issuedAt: String = "",
    val jti: String = "jti",

    val scope: String = "",

    val claims: Array<String> = []
)