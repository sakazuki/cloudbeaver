package io.cloudbeaver.service.security.external.remote.model.role;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.cloudbeaver.model.user.WebRole;

import java.util.Set;

public class DCRoleUpdateRequest {
    private final String name;
    private final String description;
    private final Set<String> permissions;

    @JsonCreator
    public DCRoleUpdateRequest(@JsonProperty("name") String name,
                               @JsonProperty("description") String description,
                               @JsonProperty("permissions") Set<String> permissions) {
        this.name = name;
        this.description = description;
        this.permissions = permissions;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public static DCRoleUpdateRequest fromWebRole(WebRole webRole) {
        return new DCRoleUpdateRequest(
            webRole.getName(),
            webRole.getDescription(),
            webRole.getPermissions()
        );
    }

    public static WebRole toWebRole(DCRoleUpdateRequest updateRequest, String roleId) {
        WebRole webRole = new WebRole(roleId);
        webRole.setDescription(updateRequest.getDescription());
        webRole.setName(updateRequest.getName());
        webRole.setPermissions(updateRequest.getPermissions());
        return webRole;
    }
}
