package com.kapitonov.restapi.controller.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.kapitonov.restapi.model.UserInfo
import com.kapitonov.restapi.service.UserService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.security.Principal

@ExtendWith(SpringExtension::class)
@WebMvcTest(MeController::class)
@AutoConfigureMockMvc(addFilters = false)
internal class MeControllerTest {
    @MockkBean
    lateinit var mockkUserService: UserService

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val username = "user@example.com"

    private val mockPrincipal = mockk<Principal> {
        every { name } returns username
    }

    @Test
    fun `when the user exists, the user info is returned`() {
        val expectedUserInfo = UserInfo(id = 3L, userId = 4L, email = username, profileImage = "url")
        val objectMapper = ObjectMapper()
        val userInfoJSON = objectMapper.writeValueAsString(expectedUserInfo)
        every { mockkUserService.getUserInfo(username) } returns expectedUserInfo
        performMe()
                .andExpect(status().isOk)
                .andExpect(content().json(userInfoJSON))
                .andReturn()

        verify { mockkUserService.getUserInfo(username) }
    }

    @Test
    fun `when the user does not exist, there is a not found error`() {
        every { mockkUserService.getUserInfo(username) } returns null

        performMe()
                .andExpect(status().isNotFound)
                .andReturn()

        verify { mockkUserService.getUserInfo(username) }
    }

    private fun performMe() =
            mockMvc.perform(MockMvcRequestBuilders.get("/user/me")
                    .principal(mockPrincipal)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            )
}