package com.kapitonov.restapi.dao

import com.kapitonov.restapi.model.Role
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleDao : CrudRepository<Role, Long> {
    fun findByName(name: String): Role
}