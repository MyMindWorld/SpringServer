package ru.protei.scriptServer.service

import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.springframework.test.context.junit4.SpringRunner
import ru.protei.scriptServer.repository.RoleRepository
import ru.protei.scriptServer.repository.UserRepository

//@SpringBootTest
//@AutoConfigureMockMvc
@RunWith(SpringRunner::class)
//@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration::class, DataSourceTransactionManagerAutoConfiguration::class])
abstract class BaseServiceTest {

    @Mock
    var userRepository: UserRepository = Mockito.mock(UserRepository::class.java)

    @Mock
    var roleRepository: RoleRepository = Mockito.mock(RoleRepository::class.java)



    @Before
    fun init() {
        MockitoAnnotations.initMocks(this);
    }

}