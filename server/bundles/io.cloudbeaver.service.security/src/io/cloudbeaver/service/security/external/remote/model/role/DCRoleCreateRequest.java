package io.cloudbeaver.service.security.external.remote.model.role;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DCRoleCreateRequest {
    private final DCRole role;
    private final String grantor;

    @JsonCreator
    public DCRoleCreateRequest(@JsonProperty("role") DCRole role,
                               @JsonProperty("grantor") String grantor) {
        this.role = role;
        this.grantor = grantor;
    }

    public DCRole getRole() {
        return role;
    }

    public String getGrantor() {
        return grantor;
    }
}
