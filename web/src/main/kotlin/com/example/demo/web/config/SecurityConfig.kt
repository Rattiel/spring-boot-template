package com.example.demo.web.config

import com.example.demo.web.core.SecurityExceptionHandler
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.savedrequest.NullRequestCache

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity, securityExceptionHandler: SecurityExceptionHandler
    ): SecurityFilterChain {
        http {
            anonymous {
                disable()
            }
            authorizeHttpRequests {
                authorize("/actuator/**", permitAll)
                authorize("/docs/**", permitAll)

                authorize(HttpMethod.GET, "/category", permitAll)
                authorize(HttpMethod.POST, "/category", hasAuthority("SCOPE_category:write"))
                authorize(HttpMethod.GET, "/category/*", permitAll)
                authorize(HttpMethod.PUT, "/category/*", hasAuthority("SCOPE_category:write"))
                authorize(HttpMethod.DELETE, "/category/*", hasAuthority("SCOPE_category:write"))

                authorize(HttpMethod.GET, "/post", permitAll)
                authorize(HttpMethod.POST, "/post", authenticated)
                authorize(HttpMethod.GET, "/post/*", permitAll)
                authorize(HttpMethod.PUT, "/post/*", authenticated)
                authorize(HttpMethod.DELETE, "/post/*", authenticated)

                authorize(anyRequest, permitAll)
            }
            cors {
                disable()
            }
            csrf {
                disable()
            }
            formLogin {
                disable()
            }
            httpBasic {
                disable()
            }
            logout {
                disable()
            }
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
            requestCache {
                requestCache = NullRequestCache()
            }
            oauth2ResourceServer {
                jwt { }
            }
            exceptionHandling {
                accessDeniedHandler = securityExceptionHandler
                authenticationEntryPoint = securityExceptionHandler
            }
        }

        return http.build()
    }

    @Bean
    fun securityExceptionHandler(
        objectMapper: ObjectMapper, messageSource: MessageSource
    ): SecurityExceptionHandler {
        return SecurityExceptionHandler(objectMapper, messageSource)
    }
}