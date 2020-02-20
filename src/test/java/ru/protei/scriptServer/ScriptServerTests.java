package ru.protei.scriptServer;

import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.protei.scriptServer.model.User;

import javax.naming.ldap.LdapName;

@SpringBootTest(classes = ScriptServer.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ScriptServerTests {

    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;

    private String getRootUrl() {
        return "http://localhost:" + port + "/api";
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void testGetAllusers() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/users",
                HttpMethod.GET, entity, String.class);
        Assert.assertNotNull(response.getBody());
    }

    @Test
    public void testGetDeveloperById() {
        User user = restTemplate.getForObject(getRootUrl() + "/users/1", User.class);
        System.out.println(user.getUsername());
        Assert.assertNotNull(user);
    }

    @SneakyThrows
    @Test
    public void testCreateDeveloper() {
        User user = new User();
        user.setUsername("DevTestName");
        user.setLdapName("DevTest");
        user.setEmail("DevTest@Test.test");
        ResponseEntity<User> postResponse = restTemplate.postForEntity(getRootUrl() + "/users", user, User.class);
        Assert.assertNotNull(postResponse);
        Assert.assertNotNull(postResponse.getBody());
    }

    @Test
    public void testUpdatePost() {
        int id = 1;
        User user = restTemplate.getForObject(getRootUrl() + "/users/" + id, User.class);
        user.setUsername("DevUpd");
        user.setEmail("DevTest@Test.test");
        restTemplate.put(getRootUrl() + "/users/" + id, user);
        User updatedUser = restTemplate.getForObject(getRootUrl() + "/users/" + id, User.class);
        Assert.assertNotNull(updatedUser);
    }

    @Test
    public void testDeletePost() {
        int id = 2;
        User user = restTemplate.getForObject(getRootUrl() + "/users/" + id, User.class);
        Assert.assertNotNull(user);
        restTemplate.delete(getRootUrl() + "/users/" + id);
        try {
            user = restTemplate.getForObject(getRootUrl() + "/users/" + id, User.class);
        } catch (final HttpClientErrorException e) {
            Assert.assertEquals(e.getStatusCode(),
                    HttpStatus.NOT_FOUND);
        }
    }


}
