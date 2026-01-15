package com.example.demo.web.test.support

import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertInstanceOf
import org.junit.jupiter.api.assertNull
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import java.time.Instant

@ExtendWith(MockKExtension::class)
class WithJwtSecurityContextFactoryTests {
    @MockK
    lateinit var withJwt: WithJwt

    lateinit var factory: WithJwtSecurityContextFactory

    @BeforeEach
    fun setup() {
        factory = WithJwtSecurityContextFactory()
    }

    @DisplayName("기본 설정으로 SecurityContext를 생성하면 어노테이션의 기본값들이 매핑된다")
    @Test
    fun `should create SecurityContext with default annotation values`() {
        // given
        every { withJwt.issuer } returns "https://issuer.example.com"
        every { withJwt.subject } returns "mock-test-subject"
        every { withJwt.audience } returns arrayOf("https://audience.example.com")
        every { withJwt.expiresAt } returns ""
        every { withJwt.notBefore } returns ""
        every { withJwt.issuedAt } returns ""
        every { withJwt.jti } returns "jti"
        every { withJwt.scope } returns ""
        every { withJwt.claims } returns arrayOf()

        // when
        val context = factory.createSecurityContext(withJwt)

        // then
        val jwt = assertInstanceOf<Jwt>(context.authentication.principal)
        assertEquals("https://issuer.example.com", jwt.issuer.toString())
        assertEquals("mock-test-subject", jwt.subject)
        assertIterableEquals(listOf("https://audience.example.com"), jwt.audience)
        assertNull(jwt.expiresAt)
        assertNull(jwt.notBefore)
        assertNull(jwt.issuedAt)
        assertEquals("jti", jwt.id)

        assertTrue(context.authentication.authorities.isEmpty())
    }

    @DisplayName("커스텀 Claims(key=value)가 주어지면 JWT Claim 맵에 포함된다")
    @Test
    fun `should parse and add custom claims to jwt`() {
        // given
        every { withJwt.issuer } returns ""
        every { withJwt.subject } returns ""
        every { withJwt.audience } returns arrayOf()
        every { withJwt.expiresAt } returns ""
        every { withJwt.notBefore } returns ""
        every { withJwt.issuedAt } returns ""
        every { withJwt.jti } returns ""
        every { withJwt.scope } returns ""
        every { withJwt.claims } returns arrayOf("email=test@example.com", "group=dev")

        // when
        val context = factory.createSecurityContext(withJwt)

        // then
        val jwt = assertInstanceOf<Jwt>(context.authentication.principal)
        assertNull(jwt.issuer)
        assertNull(jwt.subject)
        assertNull(jwt.audience)
        assertNull(jwt.expiresAt)
        assertNull(jwt.notBefore)
        assertNull(jwt.issuedAt)
        assertNull(jwt.id)
        assertEquals("test@example.com", jwt.claims["email"])
        assertEquals("dev", jwt.claims["group"])

        assertTrue(context.authentication.authorities.isEmpty())
    }

    @DisplayName("유효한 시간 문자열이 주어지면 Instant로 파싱되어 설정된다")
    @Test
    fun `should parse timestamp strings into Instant correctly`() {
        // given
        val expectedExpiresAt = "2099-12-31T23:59:59Z"
        val expectedNotBefore = "2100-01-01T00:00:00Z"
        val expectedIssuedAt = "2000-01-01T00:00:00Z"

        every { withJwt.issuer } returns ""
        every { withJwt.subject } returns ""
        every { withJwt.audience } returns arrayOf()
        every { withJwt.expiresAt } returns expectedExpiresAt
        every { withJwt.notBefore } returns expectedNotBefore
        every { withJwt.issuedAt } returns expectedIssuedAt
        every { withJwt.jti } returns ""
        every { withJwt.scope } returns ""
        every { withJwt.claims } returns arrayOf()

        // when
        val context = factory.createSecurityContext(withJwt)

        // then
        val jwt = assertInstanceOf<Jwt>(context.authentication.principal)
        assertNull(jwt.issuer)
        assertNull(jwt.subject)
        assertNull(jwt.audience)
        assertEquals(Instant.parse(expectedExpiresAt), jwt.expiresAt)
        assertEquals(Instant.parse(expectedNotBefore), jwt.notBefore)
        assertEquals(Instant.parse(expectedIssuedAt), jwt.issuedAt)
        assertNull(jwt.id)

        assertTrue(context.authentication.authorities.isEmpty())
    }

    @DisplayName("scope 클레임이 주어지면 SCOPE_ 접두사가 붙은 권한 목록으로 변환된다")
    @Test
    fun `should convert scope claims into authorities with the SCOPE prefix`() {
        // given
        every { withJwt.issuer } returns ""
        every { withJwt.subject } returns ""
        every { withJwt.audience } returns arrayOf()
        every { withJwt.expiresAt } returns ""
        every { withJwt.notBefore } returns ""
        every { withJwt.issuedAt } returns ""
        every { withJwt.jti } returns ""
        every { withJwt.scope } returns "profile email phone"
        every { withJwt.claims } returns arrayOf()

        // when
        val context = factory.createSecurityContext(withJwt)

        // then
        val jwt = assertInstanceOf<Jwt>(context.authentication.principal)
        assertNull(jwt.issuer)
        assertNull(jwt.subject)
        assertNull(jwt.audience)
        assertNull(jwt.expiresAt)
        assertNull(jwt.notBefore)
        assertNull(jwt.issuedAt)
        assertNull(jwt.id)

        val expectedAuthorities = listOf("SCOPE_profile", "SCOPE_email", "SCOPE_phone")
        val actualAuthorities = context.authentication.authorities.map(GrantedAuthority::getAuthority)
        assertIterableEquals(expectedAuthorities, actualAuthorities)
    }
}