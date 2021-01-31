package ru.protei.scriptServer.service

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import ru.protei.scriptServer.testData.dbUser
import ru.protei.scriptServer.testData.defaultRole


class CustomUserDetailsServiceTest : BaseServiceTest() {

    @InjectMocks
    lateinit var customUserDetailsService: CustomUserDetailsService



    @Disabled // TODO Verify ldap login with user details
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
    fun `when user details is not available UsernameNotFoundException is thrown`() {
        assertThrows<UsernameNotFoundException> { customUserDetailsService.loadUserByUsername("RandomUser") }
            .also { assertEquals("User RandomUser is not found!", it.message) }
    }

    @Test
    fun `when user from db tries to log in should return user roles`() {
        Mockito.`when`(userRepository.findByUsernameEquals("AuthorizedUser")).thenReturn(dbUser)

        val user: UserDetails = customUserDetailsService.loadUserByUsername("AuthorizedUser")

        assertEquals(user.username, "TestUser")
        assertEquals(user.isEnabled, true)
        assertEquals(user.authorities.first().authority, "TESTS_IN_DB_USER")
    }

}