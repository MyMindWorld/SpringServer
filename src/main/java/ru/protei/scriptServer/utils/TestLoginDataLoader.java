package ru.protei.scriptServer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.protei.scriptServer.model.Privilege;
import ru.protei.scriptServer.model.Role;
import ru.protei.scriptServer.model.User;
import ru.protei.scriptServer.repository.RoleRepository;
import ru.protei.scriptServer.service.LogService;
import ru.protei.scriptServer.service.PrivilegeService;
import ru.protei.scriptServer.service.RoleService;
import ru.protei.scriptServer.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class TestLoginDataLoader {
    Logger logger = LoggerFactory.getLogger(TestLoginDataLoader.class);

    @Autowired
    private LogService logService;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void fillTestLogsAndPrivileges() {
        Integer logRepSizeForTest = 500;
        Integer privilegeRepSizeForTest = 70;

        if (logService.logSize() < logRepSizeForTest) {
            for (int count = 0; count < logRepSizeForTest; count++) {
                int leftLimit = 97; // letter 'a'
                int rightLimit = 122; // letter 'z'
                int paramsLength = 500;
                int errorLength = 500;
                Random random = new Random();
                StringBuilder bufferError = new StringBuilder(errorLength);
                StringBuilder bufferParams = new StringBuilder(errorLength);
                for (int i = 0; i < errorLength; i++) {
                    int randomLimitedInt = leftLimit + (int)
                            (random.nextFloat() * (rightLimit - leftLimit + 1));
                    bufferError.append((char) randomLimitedInt);
                }
                for (int i = 0; i < paramsLength; i++) {
                    int randomLimitedInt = leftLimit + (int)
                            (random.nextFloat() * (rightLimit - leftLimit + 1));
                    bufferParams.append((char) randomLimitedInt);
                }
                String genError = bufferError.toString();
                String genParams = bufferParams.toString();

                logService.logAction("Test", "127.5.5.5", "TestNumber " + count, genParams, genError);
            }

        }

        if (privilegeService.returnAllPrivileges().size() < privilegeRepSizeForTest) {
            for (int count = 0; count < privilegeRepSizeForTest; count++) {
                int leftLimit = 97; // letter 'a'
                int rightLimit = 122; // letter 'z'
                int privilegeNameLength = 20;
                Random random = new Random();
                StringBuilder privilegeName = new StringBuilder(privilegeNameLength);
                for (int i = 0; i < privilegeNameLength; i++) {
                    int randomLimitedInt = leftLimit + (int)
                            (random.nextFloat() * (rightLimit - leftLimit + 1));
                    privilegeName.append((char) randomLimitedInt);
                }

                String genName = privilegeName.toString();

                privilegeService.createPrivilegeIfNotFound(genName);
            }

        }
    }


}
