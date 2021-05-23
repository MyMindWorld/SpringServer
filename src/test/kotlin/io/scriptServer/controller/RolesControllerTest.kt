package io.scriptServer.controller

import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import io.scriptServer.model.Privilege
import io.scriptServer.testData.dbUserRole
import io.scriptServer.testData.defaultRole

class RolesControllerTest : BaseControllerTest() {
    @Test
    fun shouldReturnModelWithAllRolesAndPrivileges() {
        Mockito.`when`(roleRepository.findAll()).thenReturn(listOf(defaultRole, dbUserRole))
        Mockito.`when`(privilegeRepository.findAll()).thenReturn(listOf(Privilege("test1"), Privilege("test2")))
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/roles"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.view().name("roles"))
            .andExpect(MockMvcResultMatchers.forwardedUrl("/WEB-INF/views/roles.jsp"))
            .andExpect(MockMvcResultMatchers.model().attribute("roles", listOf(defaultRole, dbUserRole)))
            .andExpect(
                MockMvcResultMatchers.model().attribute("privileges", listOf(Privilege("test1"), Privilege("test2")))
            )

        Mockito.verify(roleRepository, Mockito.times(1)).findAll()
        Mockito.verifyNoMoreInteractions(roleRepository)
    }

}