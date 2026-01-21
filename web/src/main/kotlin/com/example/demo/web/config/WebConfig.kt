package com.example.demo.web.config

import com.example.demo.web.core.AuthenticationUserResolver
import com.example.demo.web.jackson.WebJacksonModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import tools.jackson.databind.module.SimpleModule

@Configuration(proxyBeanMethods = false)
class WebConfig: WebMvcConfigurer {
    @Bean
    fun webJackson2Module(): SimpleModule {
        return WebJacksonModule()
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(AuthenticationUserResolver())
    }
}