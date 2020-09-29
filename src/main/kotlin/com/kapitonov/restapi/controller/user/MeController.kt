package com.kapitonov.restapi.controller.user

import com.kapitonov.restapi.model.UserInfo
import com.kapitonov.restapi.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/user")
class MeController {
    @Autowired
    lateinit var userService: UserService

    @RequestMapping(value = ["/me"], method = [(RequestMethod.GET)])
    fun userInfo(request: HttpServletRequest): ResponseEntity<UserInfo> =
            userService.getUserInfo(request.userPrincipal.name)?.let {
                ResponseEntity.ok(it)
            } ?: ResponseEntity.notFound().build()
}