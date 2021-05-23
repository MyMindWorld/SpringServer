package ru.protei.scriptServer.service

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import ru.protei.scriptServer.testData.dbUser
import ru.protei.scriptServer.testData.defaultRole

@SpringBootTest(classes = [CustomUserDetailsService::class])
class CustomUserDetailsServiceTest : BaseServiceTest() {

    @MockBean
    lateinit var userService: UserService// = Mockito.mock(UserRepository::class.java)

    @MockBean
    lateinit var roleService: RoleService// = Mockito.mock(RoleRepository::class.java)

    @Autowired
    lateinit var customUserDetailsService: CustomUserDetailsService

    //    @Disabled // TODO Verify ldap login with user details
    @Test
    fun `when ldap user tries to log in should return default roles`() {
        Mockito.`when`(userService.findByUsernameEquals("Vasya")).thenReturn(null)
        Mockito.`when`(roleService.findByNameEquals("ROLE_USER")).thenReturn(defaultRole)

        val user: UserDetails = customUserDetailsService.loadUserByUsername("Vasya")

        assertEquals(user.username, "Vasya")
        assertEquals(user.isEnabled, true)
        assertEquals(user.authorities.size, 1)
        assertEquals(user.authorities.first().authority, "TESTS_WRITE_AUTHORITY")
    }

    @Disabled
    fun `when user details is not available UsernameNotFoundException is thrown`() {
        assertThrows<UsernameNotFoundException> { customUserDetailsService.loadUserByUsername("RandomUser") }
            .also { assertEquals("User RandomUser is not found!", it.message) }
    }

    @Test
    fun `when user from db tries to log in should return user roles`() {
        Mockito.`when`(userService.findByUsernameEquals("AuthorizedUser")).thenReturn(dbUser)

        val user: UserDetails = customUserDetailsService.loadUserByUsername("AuthorizedUser")

        assertEquals(user.username, "TestUser")
        assertEquals(user.isEnabled, true)
        assertEquals(user.authorities.first().authority, "TESTS_IN_DB_USER")
    }

}