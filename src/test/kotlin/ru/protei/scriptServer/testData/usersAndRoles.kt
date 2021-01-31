package ru.protei.scriptServer.testData

import ru.protei.scriptServer.model.Privilege
import ru.protei.scriptServer.model.Role
import ru.protei.scriptServer.model.User
import java.util.*

val dbUserRole: Role =
    Role(1L, "ROLE_USER", false, null, Collections.singletonList(Privilege("TESTS_IN_DB_USER")))
val dbUser =
    User(1L, "TestUser", "TestUserName", "TestUser@email.com", "TestPassword", true, null, listOf(dbUserRole))


val defaultRole: Role =
    Role(1L, "TestRoleName", false, null, Collections.singletonList(Privilege("DEFAULT_PRIVILEGE")))

val ldapUserWithoutDbReference =
    User(1L, "LdapUser", "LdapUser", "LdapUser@email.com", "LdapPassword", true, null, listOf(defaultRole))

val ldapUserWithDbReference =
    User(1L, "LdapAndDbUser", "LdapAndDbUser", "LdapAndDbUser@email.com", "LdapAndDbUser", true, null, listOf(dbUserRole))