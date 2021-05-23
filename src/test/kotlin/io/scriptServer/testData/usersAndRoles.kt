package io.scriptServer.testData

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import io.scriptServer.model.Privilege
import io.scriptServer.model.Role
import io.scriptServer.model.User
import java.util.*
const val DEFAULT_PRIVILEGE_NAME = "TESTS_IN_DB_USER"
val dbUserRole: Role =
    Role(1L, "ROLE_USER", false, null, Collections.singletonList(Privilege(DEFAULT_PRIVILEGE_NAME)))
val dbUserAuthorities: List<GrantedAuthority> =
    listOf(SimpleGrantedAuthority("TESTS_IN_DB_USER"))
val dbUser =
    User(1L, "TestUser", "TestUserName", "TestUser@email.com", "TestPassword", true, null, listOf(dbUserRole))


val defaultRole: Role =
    Role(1L, "TestRoleName", false, null, Collections.singletonList(Privilege("DEFAULT_PRIVILEGE")))
val defaultRoleAuthorities: List<GrantedAuthority> =
    listOf(SimpleGrantedAuthority("DEFAULT_PRIVILEGE"))
val roleWithAdminAndDefaultPrivilege: Role =
    Role(1L, "TestRoleName", false, null, listOf(Privilege("DEFAULT_PRIVILEGE"), Privilege("ADMIN_PRIVILEGE")))
val ldapUserWithoutDbReference =
    User(1L, "LdapUser", "LdapUser", "LdapUser@email.com", "LdapPassword", true, null, listOf(defaultRole))

val ldapUserWithDbReference =
    User(
        1L,
        "LdapAndDbUser",
        "LdapAndDbUser",
        "LdapAndDbUser@email.com",
        "LdapAndDbUser",
        true,
        null,
        listOf(dbUserRole)
    )