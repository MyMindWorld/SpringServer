package ru.protei.scriptServer.service

import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import ru.protei.scriptServer.model.Privilege
import ru.protei.scriptServer.model.Role
import ru.protei.scriptServer.repository.RoleRepository
import ru.protei.scriptServer.testData.dbUser
import ru.protei.scriptServer.testData.defaultRole
import ru.protei.scriptServer.testData.ldapUserWithoutDbReference
import ru.protei.scriptServer.testData.roleWithAdminAndDefaultPrivilege

@SpringBootTest(classes = [RoleService::class])
@ExtendWith(SpringExtension::class)
class RoleServiceTest {

    @MockBean
    lateinit var privilegeService: PrivilegeService// = Mockito.mock(PrivilegeService::class.java)

    @MockBean
    lateinit var roleRepository: RoleRepository// = Mockito.mock(PrivilegeService::class.java)

    @Autowired
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


    @Test // Needs verification
    fun deleteRoleFromUsers() {
        val roleAssignedToUsers = defaultRole
        roleAssignedToUsers.users = listOf(dbUser, ldapUserWithoutDbReference)

        roleService.deleteRoleFromUsers(roleAssignedToUsers)

        Mockito.verify(roleRepository).delete(defaultRole)

        Assert.assertEquals(null, defaultRole.users)
    }

    @Test
    fun `should return role with same privileges when exists`() {
        Mockito.`when`(roleRepository.findAll()).thenReturn(listOf(defaultRole))

        val similarRole = roleService.findRoleByPrivileges(defaultRole.privileges.toList())

        Assert.assertEquals(defaultRole, similarRole.get())
    }

    @Test
    fun `should not return role with different privileges`() {
        Mockito.`when`(roleRepository.findAll()).thenReturn(listOf(roleWithAdminAndDefaultPrivilege))

        val similarRole = roleService.findRoleByPrivileges(defaultRole.privileges.toList())

        Assert.assertEquals(false, similarRole.isPresent)
    }

    @Test
    fun updateRoleAllPrivileges() {
    }
}