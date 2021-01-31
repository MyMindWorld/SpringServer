package ru.protei.scriptServer.config

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.util.ReflectionTestUtils
import ru.protei.scriptServer.repository.UserRepository
import ru.protei.scriptServer.service.UserService
import ru.protei.scriptServer.testData.dbUser
import ru.protei.scriptServer.testData.defaultRole
import ru.protei.scriptServer.testData.ldapUserWithoutDbReference


@RunWith(SpringRunner::class)
class CustomLdapAuthTest {
    @Mock
    var userRepository: UserRepository = Mockito.mock(UserRepository::class.java)

    @Mock
    var userService: UserService = Mockito.mock(UserService::class.java)

    @Spy
    lateinit var customLdapAuth: CustomLdapAuth


    @Before
    fun init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    fun `ldap auth with correct password and disabled ldap should return null`() {
        ReflectionTestUtils.setField(customLdapAuth, "enableLdapAuth", false);

        val authentication: Authentication = UsernamePasswordAuthenticationToken("TestUser", "TestPassword")
        val receivedAuth = customLdapAuth.authenticate(authentication)

        assertEquals(null, receivedAuth)
    }

    @Test
    fun `ldap auth with incorrect password and enabled ldap auth should return null`() {
        ReflectionTestUtils.setField(customLdapAuth, "enableLdapAuth", true)
        val authentication: Authentication = UsernamePasswordAuthenticationToken("TestUser", "TestPassword")
        Mockito.doReturn(false).`when`(customLdapAuth).ldapAuth(Mockito.any(), Mockito.any())

        val receivedAuth = customLdapAuth.authenticate(authentication)

        assertEquals(null, receivedAuth)
    }

    @Test
    fun `ldap auth with correct password for the first time should return UPAT`() {
        ReflectionTestUtils.setField(customLdapAuth, "enableLdapAuth", true)
        ReflectionTestUtils.setField(customLdapAuth, "userRepository", userRepository)
        ReflectionTestUtils.setField(customLdapAuth, "userService", userService)



        val authentication: Authentication =
            UsernamePasswordAuthenticationToken("LdapUser", "LdapPassword", defaultRole.authorities)
        Mockito.`when`(userRepository.findByUsernameEquals("LdapUser")).thenReturn(null)
        Mockito.`when`(userService.createUser("LdapUser", "LdapPassword")).thenReturn(ldapUserWithoutDbReference)
        Mockito.doReturn(true).`when`(customLdapAuth).ldapAuth(Mockito.any(), Mockito.any())

        val receivedAuth = customLdapAuth.authenticate(authentication)

        assertEquals(authentication.name, receivedAuth.name)
        assertEquals(authentication.principal, receivedAuth.principal)
        assertEquals(authentication.authorities, receivedAuth.authorities)
    }

    @Test
    fun `ldap auth with correct password for the second time should return correct user roles`() {
        ReflectionTestUtils.setField(customLdapAuth, "enableLdapAuth", true)
        ReflectionTestUtils.setField(customLdapAuth, "userRepository", userRepository)
        ReflectionTestUtils.setField(customLdapAuth, "userService", userService)



        val authentication: Authentication =
            UsernamePasswordAuthenticationToken("TestUser", "TestPassword", dbUser.authorities)
        Mockito.`when`(userRepository.findByUsernameEquals("TestUser")).thenReturn(dbUser)
        Mockito.verify(userService,never()).createUser("TestUser", "TestPassword")
        Mockito.doReturn(true).`when`(customLdapAuth).ldapAuth("TestUser", "TestPassword")

        val receivedAuth = customLdapAuth.authenticate(authentication)

        assertEquals(authentication.name, receivedAuth.name)
        assertEquals(authentication.principal, receivedAuth.principal)
        assertEquals(authentication.authorities, receivedAuth.authorities)
    }
}