package com.kapitonov.restapi.dao

import com.kapitonov.restapi.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDao : CrudRepository<User, Long> {
    fun findOneByUsername(email: String): User?
}