package com.example.demo.web

import com.example.demo.config.TestContainerConfig
import com.example.demo.web.request.PostRequest
import com.example.demo.web.test.support.WithJwt
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import org.springframework.transaction.annotation.Transactional
import tools.jackson.databind.ObjectMapper

@AutoConfigureMockMvc
@Import(TestContainerConfig::class)
@SpringBootTest
@Transactional
class PostApiTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @DisplayName("게시글을 생성할 수 있다")
    @Test
    @WithJwt(subject = "new-user")
    fun `should create post`() {
        // given
        val request = PostRequest(categoryId = 3L)

        // when & then
        mockMvc.post("/post") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }
    }

    @DisplayName("게시글을 단건 조회할 수 있다")
    @Test
    fun `should get post by id`() {
        // given
        val id = 1L

        // when & then
        mockMvc.get("/post/{id}", id).andExpect {
            status { isOk() }
        }
    }

    @DisplayName("카테고리별 게시글 목록을 조회할 수 있다")
    @Test
    fun `should get posts by category`() {
        // given
        val categoryId = 3L
        val page = 0
        val size = 10
        val sort = "id,desc"

        // when & then
        mockMvc.get("/post") {
            param("categoryId", categoryId.toString())
            param("page", page.toString())
            param("size", size.toString())
            param("sort", sort)
        }.andExpect {
            status { isOk() }
        }
    }

    @DisplayName("해당 게시글을 수정할 수 있다")
    @Test
    @WithJwt(subject = "test-user-2")
    fun `should update post`() {
        // given
        val id = 2L
        val request = PostRequest(categoryId = 3L)

        // when & then
        mockMvc.put("/post/{id}", id) {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }
    }

    @DisplayName("해당 게시글을 삭제할 수 있다")
    @Test
    @WithJwt(subject = "test-user-3")
    fun `should delete post`() {
        // given
        val id = 3L

        // when & then
        mockMvc.delete("/post/{id}", id).andExpect {
            status { isNoContent() }
        }
    }
}