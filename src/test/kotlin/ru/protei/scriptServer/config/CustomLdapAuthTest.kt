package ru.protei.scriptServer.config

import org.junit.Assert.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Spy
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.util.ReflectionTestUtils
import ru.protei.scriptServer.repository.UserRepository
import ru.protei.scriptServer.service.UserService
import ru.protei.scriptServer.testData.dbUser
import ru.protei.scriptServer.testData.dbUserAuthorities
import ru.protei.scriptServer.testData.defaultRoleAuthorities
import ru.protei.scriptServer.testData.ldapUserWithoutDbReference


@SpringBootTest(classes = [CustomLdapAuth::class])
@ExtendWith(SpringExtension::class)
class CustomLdapAuthTest {
    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var userService: UserService

    @Spy
    lateinit var customLdapAuth: CustomLdapAuth


    @Nested
    inner class WhenLdapAuthIsDisabled {

        @BeforeEach
        fun init() {
            ReflectionTestUtils.setField(customLdapAuth, "enableLdapAuth", false);
        }

        @Test
        fun `ldap auth with correct password and disabled ldap should return null`() {


            val authentication: Authentication = UsernamePasswordAuthenticationToken("TestUser", "TestPassword")
            val receivedAuth = customLdapAuth.authenticate(authentication)

            assertEquals(null, receivedAuth)
        }
    }

    @Nested
    inner class WhenLdapAuthIsEnabled {

        @BeforeEach
        fun init() {
            ReflectionTestUtils.setField(customLdapAuth, "enableLdapAuth", true)
            ReflectionTestUtils.setField(customLdapAuth, "userRepository", userRepository)
            ReflectionTestUtils.setField(customLdapAuth, "userService", userService)
        }

        @Test
        fun `ldap auth with incorrect password and enabled ldap auth should return null`() {
            val authentication: Authentication = UsernamePasswordAuthenticationToken("TestUser", "TestPassword")
            Mockito.doReturn(false).`when`(customLdapAuth).ldapAuth(Mockito.any(), Mockito.any())

            val receivedAuth = customLdapAuth.authenticate(authentication)

            assertEquals(null, receivedAuth)
        }

        @Test
        fun `ldap auth with correct password for the first time should return UPAT`() {
            val authentication: Authentication =
                UsernamePasswordAuthenticationToken("LdapUser", "LdapPassword", defaultRoleAuthorities)
            Mockito.`when`(userRepository.findByUsernameEquals("LdapUser")).thenReturn(null)
            Mockito.`when`(userService.createUser("LdapUser", "LdapPassword")).thenReturn(ldapUserWithoutDbReference)
            Mockito.doReturn(true).`when`(customLdapAuth).ldapAuth(Mockito.any(), Mockito.any())

            val receivedAuth = customLdapAuth.authenticate(authentication)

            assertEquals(authentication.name, receivedAuth.name)
            assertEquals(authentication.principal, receivedAuth.principal)
            assertEquals(authentication.credentials, receivedAuth.credentials)
        }

        @Test
        fun `ldap auth with correct password for the second time should return correct user roles and update password in db`() {
            val authentication: Authentication =
                UsernamePasswordAuthenticationToken("TestUser", "LdapPassword", dbUserAuthorities)
            Mockito.`when`(userRepository.findByUsernameEquals("TestUser")).thenReturn(dbUser)
            Mockito.doReturn(true).`when`(customLdapAuth).ldapAuth("TestUser", "LdapPassword")

            val receivedAuth = customLdapAuth.authenticate(authentication)

            Mockito.verify(userService, Mockito.never()).createUser("TestUser", "LdapPassword")
            Mockito.verify(userService).changeUserPassword(dbUser, "LdapPassword")
            assertEquals(dbUser.password, receivedAuth.credentials)
        }

    }

}