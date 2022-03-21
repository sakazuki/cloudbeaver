package io.cloudbeaver.service.security.internal.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.cloudbeaver.model.user.WebRole;
import io.cloudbeaver.service.security.external.remote.model.role.DCRole;
import io.cloudbeaver.service.security.external.remote.model.role.DCRoleCreateRequest;
import io.cloudbeaver.service.security.external.remote.model.role.DCRoleUpdateRequest;
import okhttp3.*;
import org.jkiss.dbeaver.model.exec.DBCException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class DCServerClient {
    private static final String ADMIN_ROLES_ENDPOINTS = "/security/admin/roles";

    private static final Gson gson = new GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .create();

    private static final OkHttpClient httpClient = new OkHttpClient().newBuilder()
        .build();

    private final String dcServerUrl;

    public DCServerClient(String dcServerUrl) {
        this.dcServerUrl = dcServerUrl;
    }

    public WebRole[] findAllRoles() throws DBCException {
        var request = new Request.Builder()
            .url(dcServerUrl + ADMIN_ROLES_ENDPOINTS)
            .get()
            .build();

        return executeRequest(request, new TypeToken<ArrayList<DCRole>>() {
        })
            .stream()
            .map(DCRole::toWebRole)
            .toArray(WebRole[]::new);
    }

    public void createRole(DCRoleCreateRequest roleCreateRequest) throws DBCException {
        var request = new Request.Builder()
            .url(dcServerUrl + ADMIN_ROLES_ENDPOINTS)
            .post(RequestBody.create(MediaType.parse("application/json"), gson.toJson(roleCreateRequest)))
            .build();

        executeRequest(request, Void.class);
    }

    public void updateRole(String roleId, DCRoleUpdateRequest roleUpdateRequest) throws DBCException {
        var url = HttpUrl.parse(dcServerUrl + ADMIN_ROLES_ENDPOINTS)
            .newBuilder()
            .addPathSegment(roleId)
            .build();
        var request = new Request.Builder()
            .url(url)
            .put(RequestBody.create(MediaType.parse("application/json"), gson.toJson(roleUpdateRequest)))
            .build();

        executeRequest(request, Void.class);
    }

    public WebRole findRole(String roleId) throws DBCException {
        var url = HttpUrl.parse(dcServerUrl + ADMIN_ROLES_ENDPOINTS)
            .newBuilder()
            .addPathSegment(roleId)
            .build();
        var request = new Request.Builder()
            .url(url)
            .get()
            .build();

        return DCRole.toWebRole(executeRequest(request, DCRole.class));
    }

    public void deleteRole(String roleId) throws DBCException {
        var url = HttpUrl.parse(dcServerUrl + ADMIN_ROLES_ENDPOINTS)
            .newBuilder()
            .addPathSegment(roleId)
            .build();
        var request = new Request.Builder()
            .url(url)
            .delete()
            .build();
        executeRequest(request, Void.class);
    }

    private <T> T executeRequest(Request request, Class<T> type) throws DBCException {
        var call = httpClient.newCall(request);
        try {
            Response response = call.execute();
            return gson.fromJson(response.body().string(), type);
        } catch (IOException e) {
            throw new DBCException("Error during execute dc request", e);
        }
    }

    private <T> T executeRequest(Request request, TypeToken<T> typeToken) throws DBCException {
        var call = httpClient.newCall(request);
        try {
            Response response = call.execute();
            return gson.fromJson(response.body().string(), typeToken.getType());
        } catch (IOException e) {
            throw new DBCException("Error during execute dc request", e);
        }
    }

    public static void main(String[] args) throws DBCException {
        var client = new DCServerClient("http://localhost:8080");
        client.findAllRoles();
        client.createRole(
            new DCRoleCreateRequest(
                new DCRole(
                    "test2",
                    "test2",
                    "test2",
                    Set.of("user")
                ),
                "cbadmin"));
        client.findAllRoles();
        client.updateRole("test2", new DCRoleUpdateRequest("test3", "test3", Set.of("user")));
        client.deleteRole("test2");
        client.findAllRoles();
    }
}
