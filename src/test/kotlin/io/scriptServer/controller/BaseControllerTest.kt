package io.scriptServer.controller

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import io.scriptServer.config.MessageQueueConfig
import io.scriptServer.config.ProcessQueueConfig
import io.scriptServer.repository.*
import io.scriptServer.service.*
import io.scriptServer.utils.SystemIntegration.DynamicParamsScriptsRunner
import io.scriptServer.utils.SystemIntegration.PythonScriptsRunner
import io.scriptServer.utils.Utils


@WebMvcTest(secure = false)
@ExtendWith(SpringExtension::class)
open class BaseControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var roleRepository: RoleRepository

    @MockBean
    lateinit var roleService: RoleService

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var userService: UserService

    @MockBean
    lateinit var scriptRepository: ScriptRepository

    @MockBean
    lateinit var scriptsService: ScriptsService

    @MockBean
    lateinit var venvRepository: VenvRepository

    @MockBean
    lateinit var venvService: VenvService

    @MockBean
    lateinit var userFileRepository: UserFileRepository

    @MockBean
    lateinit var storageService: StorageService

    @MockBean
    lateinit var privilegeRepository: PrivilegeRepository

    @MockBean
    lateinit var privilegeService: PrivilegeService

    @MockBean
    lateinit var logService: LogService

    @MockBean
    lateinit var logRepository: LogRepository

    @MockBean
    lateinit var utils: Utils

    @MockBean
    lateinit var simpMessagingTemplate: SimpMessagingTemplate

    @MockBean
    lateinit var javaMailSender: JavaMailSender

    @MockBean
    lateinit var messageQueueConfig: MessageQueueConfig

    @MockBean
    lateinit var processQueueConfig: ProcessQueueConfig

    @MockBean
    lateinit var dynamicParamsScriptsRunner: DynamicParamsScriptsRunner

    @MockBean
    lateinit var pythonScriptsRunner: PythonScriptsRunner

    @MockBean
    lateinit var passwordEncoder: PasswordEncoder


}