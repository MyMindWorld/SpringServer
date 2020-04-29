package ru.protei.scriptServer.service;

import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class GitLabOAuth2UserService {
    protected Map<String, Object> attributes;

    public GitLabOAuth2UserService(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    public String getName() {
        return (String) attributes.get("name");
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }

    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}
