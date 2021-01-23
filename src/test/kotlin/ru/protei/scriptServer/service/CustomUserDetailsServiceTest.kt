package ru.protei.scriptServer.service

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.springframework.security.core.userdetails.UserDetails
import ru.protei.scriptServer.model.Privilege
import ru.protei.scriptServer.model.Role
import ru.protei.scriptServer.model.User
import java.util.*


class CustomUserDetailsServiceTest : BaseServiceTest() {

    @InjectMocks
    lateinit var customUserDetailsService: CustomUserDetailsService

    private val defaultRole: Role = Role(1L, "TestRoleName", false, null, Collections.singletonList(Privilege("TESTS_WRITE_AUTHORITY")))
    private val authorizedUserRole: Role = Role(1L, "TestAuthorizedRoleName", false, null, Collections.singletonList(Privilege("TESTS_WRITE_AUTHORIZED")))
    private val authorizedUser = User(1L, "TestUsername", "Vasya", "Vasya@email.com", "passwordEncrypted", true, null, listOf(authorizedUserRole))

    @Test
    fun `when ldap user tries to log in should return default roles`() {
        Mockito.`when`(userRepository.findByUsernameEquals("Vasya")).thenReturn(null)
        Mockito.`when`(roleRepository.findByNameEquals("ROLE_USER")).thenReturn(defaultRole)

        val user: UserDetails = customUserDetailsService.loadUserByUsername("Vasya")

        assertEquals(user.username, "Vasya")
        assertEquals(user.isEnabled, true)
        assertEquals(user.authorities.size, 1)
        assertEquals(user.authorities.first().authority, "TESTS_WRITE_AUTHORITY")
    }

    @Test
    fun `when user from db tries to log in should return user roles`() {
        Mockito.`when`(userRepository.findByUsernameEquals("AuthorizedUser")).thenReturn(authorizedUser)

        val user: UserDetails = customUserDetailsService.loadUserByUsername("AuthorizedUser")

        assertEquals(user.username, "TestUsername")
        assertEquals(user.isEnabled, true)
        assertEquals(user.authorities.first().authority, "TESTS_WRITE_AUTHORIZED")
    }

}