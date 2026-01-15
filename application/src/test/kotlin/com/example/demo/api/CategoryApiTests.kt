package com.example.demo.api

import com.example.demo.config.TestContainerConfig
import com.example.demo.web.request.CategoryRequest
import com.example.demo.web.test.support.WithJwt
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.transaction.annotation.Transactional

@AutoConfigureMockMvc
@Import(TestContainerConfig::class)
@SpringBootTest
@Transactional
class CategoryApiTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @DisplayName("카테고리를 생성할 수 있다")
    @Test
    @WithJwt(scope = "category:write")
    fun `should create category`() {
        // given
        val request = CategoryRequest()

        // when & then
        mockMvc.post("/category") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }
    }

    @DisplayName("해당 카테고리를 조회할 수 있다")
    @Test
    fun `should get category`() {
        // given
        val id = 1L

        // when & then
        mockMvc.get("/category/{id}", id) {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @DisplayName("전체 카테고리 목록을 조회할 수 있다")
    @Test
    fun `should get all categories`() {
        // when & then
        mockMvc.get("/category") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }
    }

    @DisplayName("해당 카테고리를 수정할 수 있다")
    @Test
    @WithJwt(scope = "category:write")
    fun `should update category`() {
        // given
        val id = 2L
        val request = CategoryRequest()

        // when & then
        mockMvc.put("/category/{id}", id) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }
    }

    @DisplayName("해당 카테고리를 삭제할 수 있다")
    @Test
    @WithJwt(scope = "category:write")
    fun `should delete category`() {
        // given
        val id = 3L

        // when & then
        mockMvc.delete("/category/{id}", id).andExpect {
            status { isNoContent() }
        }
    }
}