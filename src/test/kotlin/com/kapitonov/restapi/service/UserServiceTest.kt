package com.kapitonov.restapi.service

import com.kapitonov.restapi.dao.RoleDao
import com.kapitonov.restapi.dao.UserDao
import com.kapitonov.restapi.dao.UserInfoDao
import com.kapitonov.restapi.model.Role
import com.kapitonov.restapi.model.User
import com.kapitonov.restapi.model.UserInfo
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@WebMvcTest(UserService::class)
@AutoConfigureMockMvc(addFilters = false)
internal class UserServiceTest {
    @MockkBean
    lateinit var mockkUserDao: UserDao

    @MockkBean
    lateinit var mockkRoleDao: RoleDao

    @MockkBean
    lateinit var mockkUserInfoDao: UserInfoDao

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var userService: UserService

    private val username = "user@example.com"

    @Test
    fun `when the user is loaded by username, the user is returned`() {
        val expectedUser = mockk<User>(relaxed = true)
        every { mockkUserDao.findOneByUsername(username) } returns expectedUser

        val userDetails = userService.loadUserByUsername(username)

        verify { mockkUserDao.findOneByUsername(username) }
        assertEquals(expectedUser.username, userDetails.username)
    }

    @Test
    fun `when the user is not found, there is a UsernameNotFoundException`() {
        every { mockkUserDao.findOneByUsername(username) } returns null

        assertThrows<UsernameNotFoundException> {
            userService.loadUserByUsername(username)
        }

        verify { mockkUserDao.findOneByUsername(username) }
    }

    @Test
    fun `when a new user is created successfully, the user info is returned`() {
        val userId = 3L
        val expectedUser = mockk<User>(relaxed = true) {
            every { id } returns userId
        }
        val userRole = mockk<Role>(relaxed = true)
        val expectedNewUserInfo = UserInfo(userId = userId, email = username)
        every { mockkUserDao.findOneByUsername(username) } returns null
        every { mockkRoleDao.findByName("USER") } returns userRole
        every { mockkUserDao.save(any<User>()) } returns expectedUser
        every { mockkUserInfoDao.save(expectedNewUserInfo) } returns expectedNewUserInfo

        val newUser = userService.createUser(username, "password")

        verify { mockkUserDao.findOneByUsername(username) }
        verify { mockkRoleDao.findByName("USER") }
        verify { mockkUserDao.save(any<User>()) }
        verify { mockkUserInfoDao.save(expectedNewUserInfo) }
        assertEquals(expectedUser, newUser)
    }

    @Test
    fun `when the new user already exists, a null is returned`() {
        val sameEmailUser = mockk<User>(relaxed = true)
        every { mockkUserDao.findOneByUsername(username) } returns sameEmailUser

        val newUser = userService.createUser(username, "password")

        verify { mockkUserDao.findOneByUsername(username) }
        assertNull(newUser)
    }

    @Test
    fun `when updating the password successfully, a true value is returned`() {
        val oldPassword = "1234"
        val newPassword = "newPassword"
        val expectedUser = mockk<User>(relaxed = true) {
            every { password } returns passwordEncoder.encode(oldPassword)
        }
        every { mockkUserDao.findOneByUsername(username) } returns expectedUser
        every { mockkUserDao.save(any<User>()) } returns expectedUser

        val passwordUpdated = userService.updatePassword(username, oldPassword, newPassword)

        verify { mockkUserDao.findOneByUsername(username) }
        verify { mockkUserDao.save(any<User>()) }
        assertTrue(passwordUpdated)
    }

    @Test
    fun `when updating the password the current password does not match, a false value is returned`() {
        val oldPassword = "wrongOldPassword"
        val newPassword = "newPassword"
        val expectedUser = mockk<User>(relaxed = true) {
            every { password } returns passwordEncoder.encode("oldPassword")
        }
        every { mockkUserDao.findOneByUsername(username) } returns expectedUser

        val passwordUpdated = userService.updatePassword(username, oldPassword, newPassword)

        verify { mockkUserDao.findOneByUsername(username) }
        assertTrue(passwordUpdated.not())
    }

    @Test
    fun `when getting the user info successfully, the info is returned`() {
        val userId = 3L
        val expectedUser = mockk<User>(relaxed = true) {
            every { id } returns userId
        }
        val expectedUserInfo = mockk<UserInfo>(relaxed = true)
        every { mockkUserDao.findOneByUsername(username) } returns expectedUser
        every { mockkUserInfoDao.findOneByUserId(userId) } returns expectedUserInfo

        val userInfo = userService.getUserInfo(username)

        verify { mockkUserDao.findOneByUsername(username) }
        verify { mockkUserInfoDao.findOneByUserId(userId) }
        assertEquals(expectedUserInfo, userInfo)
    }

    @Test
    fun `when getting the user info the info is not found, a null is returned`() {
        every { mockkUserDao.findOneByUsername(username) } returns null

        val userInfo = userService.getUserInfo(username)

        verify { mockkUserDao.findOneByUsername(username) }
        assertNull(userInfo)
    }

    @Test
    fun `when getting the user list successfully, the list is returned`() {
        val expectedUsers = listOf<UserInfo>(mockk(), mockk())
        every { mockkUserInfoDao.findAll() } returns expectedUsers

        val users = userService.getAllUsers()

        verify { mockkUserInfoDao.findAll() }
        assertEquals(expectedUsers, users)
    }
}