package ru.protei.scriptServer.service

import org.junit.Assert
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import ru.protei.scriptServer.model.Privilege
import ru.protei.scriptServer.model.Role

class RoleServiceTest : BaseServiceTest() {

    @Mock
    var privilegeService: PrivilegeService = Mockito.mock(PrivilegeService::class.java)

    @InjectMocks
    lateinit var roleService: RoleService

    @Test
    fun createRoleIfNotFound() {
        val userPrivileges = listOf(Privilege("Test1"), Privilege("Test2"))

        val expectedRole = Role.builder()
                .name("TestUserRole")
                .privileges(userPrivileges)
                .build();

        val createdRole = roleService.createRoleIfNotFound("TestUserRole", userPrivileges)

        Mockito.verify(roleRepository).save(expectedRole)

        Assert.assertEquals(createdRole.name, "TestUserRole")
        Assert.assertEquals(createdRole.privileges, userPrivileges)
    }

    @Test
    fun testCreateRoleIfNotFound() {
        val userPrivileges = listOf(Privilege("Test1"), Privilege("Test2"))

        val expectedRole = Role.builder()
                .name("TestUserRole")
                .is_protected(true)
                .privileges(userPrivileges)
                .build();

        val createdRole = roleService.createRoleIfNotFound("TestUserRole", userPrivileges,true)

        Mockito.verify(roleRepository).save(expectedRole)

        Assert.assertEquals(createdRole.name, "TestUserRole")
        Assert.assertEquals(createdRole.privileges, userPrivileges)
    }

    @Test
    fun updateRole() {
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