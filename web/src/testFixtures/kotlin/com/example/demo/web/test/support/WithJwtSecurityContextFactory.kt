package com.example.demo.web.test.support

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextHolderStrategy
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.test.context.support.WithSecurityContextFactory
import java.time.Instant

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
class WithJwtSecurityContextFactory : WithSecurityContextFactory<WithJwt> {
    @Autowired(required = false)
    var securityContextHolderStrategy: SecurityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy()

    @Autowired(required = false)
    var converter: Converter<Jwt, AbstractAuthenticationToken> = JwtAuthenticationConverter()

    override fun createSecurityContext(annotation: WithJwt): SecurityContext {
        var builder = Jwt.withTokenValue("token").header("alg", "none")
        annotation.issuer ifNotBlank { builder = builder.issuer(it) }
        annotation.subject ifNotBlank { builder = builder.subject(it) }
        annotation.audience ifNotEmpty { builder = builder.audience(it.toList()) }
        annotation.expiresAt ifNotNull { builder = builder.expiresAt(it) }
        annotation.issuedAt ifNotNull { builder = builder.issuedAt(it) }
        annotation.notBefore ifNotNull { builder = builder.notBefore(it) }
        annotation.jti ifNotBlank { builder = builder.jti(it) }
        annotation.scope ifNotBlank { builder = builder.claims(mapOf("scope" to it)) }
        annotation.claims ifNotEmpty { builder = builder.claims(it.toMap(splitter = "=")) }

        return securityContextHolderStrategy.createEmptyContext().also {
            it.authentication = converter.convert(builder.build())
        }
    }

    infix fun String.ifNotNull(action: (Instant) -> Unit) {
        if (isNotBlank()) {
            action(Instant.parse(this))
        }
    }

    infix fun String.ifNotBlank(action: (String) -> Unit) {
        if (isNotBlank()) {
            action(this)
        }
    }

    infix fun Array<String>.ifNotEmpty(action: (Array<String>) -> Unit) {
        if (isNotEmpty()) {
            action(this)
        }
    }

    fun Jwt.Builder.claims(claims: Map<String, String>): Jwt.Builder {
        return claims {
            it.putAll(claims)
        }
    }

    fun Array<String>.toMap(splitter: String = "="): Map<String, String> {
        return associateTo(mutableMapOf()) {
            it.substringBefore(splitter) to it.substringAfter(splitter, "")
        }
    }
}