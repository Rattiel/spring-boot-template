package com.example.demo.web.core

import com.example.demo.model.User
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.core.MethodParameter
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.ModelAndViewContainer

class AuthenticationUserResolverTests {
    private val resolver = AuthenticationUserResolver()

    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
        unmockkAll()
    }

    @DisplayName("supportsParameter 메서드 테스트")
    @Nested
    inner class SupportsParameterTests {
        @DisplayName("올바른 어노테이션과 User 타입이면 true를 반환한다")
        @Test
        fun `return true when has annotation and correct type`() {
            // given
            val parameter = mockk<MethodParameter>()
            every { parameter.hasParameterAnnotation(AuthenticationUser::class.java) } returns true
            every { parameter.parameterType } returns User::class.java

            // when
            val result = resolver.supportsParameter(parameter)

            // then
            assertTrue(result)
        }

        @DisplayName("어노테이션이 없으면 false를 반환한다")
        @Test
        fun `return false when annotation is missing`() {
            // given
            val parameter = mockk<MethodParameter>()
            every { parameter.hasParameterAnnotation(AuthenticationUser::class.java) } returns false
            every { parameter.parameterType } returns User::class.java

            // when
            val result = resolver.supportsParameter(parameter)

            // then
            assertFalse(result)
        }

        @DisplayName("User 타입이 아니면 false를 반환한다")
        @Test
        fun `return false when type is not User`() {
            // given
            val parameter = mockk<MethodParameter>()
            every { parameter.hasParameterAnnotation(AuthenticationUser::class.java) } returns true
            every { parameter.parameterType } returns String::class.java

            // when
            val result = resolver.supportsParameter(parameter)

            // then
            assertFalse(result)
        }
    }

    @DisplayName("resolveArgument 메서드 테스트")
    @Nested
    inner class ResolveArgumentTests {
        private val parameter = mockk<MethodParameter>()
        private val mavContainer = mockk<ModelAndViewContainer>()
        private val webRequest = mockk<NativeWebRequest>()
        private val binderFactory = mockk<WebDataBinderFactory>()

        @DisplayName("인증 정보가 없으면 null을 반환한다")
        @Test
        fun `return null when authentication is null`() {
            // given
            setupSecurityContext(null)

            // when
            val result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory)

            // then
            assertNull(result)
        }

        @DisplayName("식별할 수 없는 사용자면 null을 반환한다")
        @Test
        fun `return null when not identified`() {
            // given
            val authentication = mockk<Authentication>()
            every { authentication.isAuthenticated } returns true
            every { authentication.name } returns null
            setupSecurityContext(authentication)

            // when
            val result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory)

            // then
            assertNull(result)
        }

        @DisplayName("인증되지 않은 사용자면 null을 반환한다")
        @Test
        fun `return null when not authenticated`() {
            // given
            val authentication = mockk<Authentication>()
            every { authentication.isAuthenticated } returns false
            every { authentication.name } returns "user"
            setupSecurityContext(authentication)

            // when
            val result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory)

            // then
            assertNull(result)
        }

        private fun setupSecurityContext(authentication: Authentication?) {
            val securityContext = mockk<SecurityContext>()
            every { securityContext.authentication } returns authentication
            SecurityContextHolder.setContext(securityContext)
        }
    }
}