package com.kapitonov.restapi.dao

import com.kapitonov.restapi.model.UserInfo
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserInfoDao : CrudRepository<UserInfo, Long> {
    fun findOneByUserId(userId: Long): UserInfo?
}