package com.example.demo.web.config

import com.example.demo.web.core.AuthenticationUserResolver
import com.example.demo.web.jackson2.WebJackson2Module
import com.fasterxml.jackson.databind.module.SimpleModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration(proxyBeanMethods = false)
class WebConfig: WebMvcConfigurer {
    @Bean
    fun webJackson2Module(): SimpleModule {
        return WebJackson2Module()
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(AuthenticationUserResolver())
    }
}