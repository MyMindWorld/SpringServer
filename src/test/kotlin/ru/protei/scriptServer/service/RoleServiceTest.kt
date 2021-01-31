package ru.protei.scriptServer.service

import org.junit.Assert
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import ru.protei.scriptServer.model.Privilege
import ru.protei.scriptServer.model.Role
import ru.protei.scriptServer.testData.defaultRole

class RoleServiceTest : BaseServiceTest() {

    @Mock
    var privilegeService: PrivilegeService = Mockito.mock(PrivilegeService::class.java)

    @InjectMocks
    lateinit var roleService: RoleService

    @Test
    fun `role should be created if not found in db`() {
        val userPrivileges = listOf(Privilege("Test1"), Privilege("Test2"))

        val expectedRole = Role.builder()
            .name("TestUserRole")
            .is_protected(false)
            .privileges(userPrivileges)
            .build();

        val createdRole = roleService.createRoleIfNotFound("TestUserRole", userPrivileges)

        Mockito.verify(roleRepository).save(expectedRole)

        Assert.assertEquals(expectedRole, createdRole)
    }

    @Test
    fun `role should be created with protection if not found in db`() {
        val userPrivileges = listOf(Privilege("Test1"), Privilege("Test2"))

        val expectedRole = Role.builder()
            .name("TestUserRole")
            .is_protected(true)
            .privileges(userPrivileges)
            .build();

        val createdRole = roleService.createProtectedRoleIfNotFound("TestUserRole", userPrivileges)

        Mockito.verify(roleRepository).save(expectedRole)

        Assert.assertEquals(expectedRole, createdRole)
    }

    @Test
    fun `role should be updated if existed`() {
        val newPrivileges = listOf(Privilege("New1"), Privilege("New2"))

        Mockito.`when`(roleRepository.findByNameEquals("defaultRole")).thenReturn(defaultRole)

        val createdRole = roleService.updateRolePrivileges("defaultRole", newPrivileges)

        Mockito.verify(roleRepository).save(defaultRole)

        Assert.assertEquals(newPrivileges, createdRole.privileges)
    }

    @Test
    fun testUpdateRole() {
    }

    @Test
    fun testUpdateRole1() {
    }

    @Test
    fun deleteRoleFromUsers() {
    }

    @Test
    fun findRoleByPrivileges() {
    }

    @Test
    fun updateRoleAllPrivileges() {
    }
}