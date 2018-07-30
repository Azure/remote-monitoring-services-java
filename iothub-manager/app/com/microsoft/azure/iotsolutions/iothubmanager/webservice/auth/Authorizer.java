// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.iothubmanager.webservice.auth;

import play.libs.Json;
import play.libs.typedmap.TypedKey;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import java.util.HashMap;
import java.util.List;

/**
 * Handles authorization based on user's role and related allowed actions.
 */
public class Authorizer extends Results {

    public static final TypedKey<List<String>> ALLOWED_ACTIONS_TYPED_KEY = TypedKey.create("allowedActions");
    public static final TypedKey<Boolean> AUTH_REQUIRED_TYPED_KEY = TypedKey.create("AuthRequired");
    public static final TypedKey<Boolean> EXTERNAL_REQUEST_TYPED_KEY = TypedKey.create("ExternalRequest");

    /**
     * Retrieves user allowed actions
     *
     * @param ctx the current request context
     * @return a list of allowed actions
     */
    public List<String> getAllowedActions(Http.Context ctx) {
        return ctx.request().attrs().get(ALLOWED_ACTIONS_TYPED_KEY);
    }

    /**
     * Retrieves the configuration of auth
     *
     * @param ctx the current request context
     * @return a list of allowed actions
     */
    public Boolean isAuthRequired(Http.Context ctx) {
        return ctx.request().attrs().get(AUTH_REQUIRED_TYPED_KEY);
    }

    public Boolean isExternalRequest(Http.Context ctx) {
        return ctx.request().attrs().get(EXTERNAL_REQUEST_TYPED_KEY);
    }

    /**
     * Generates an alternative result if the user is not Authorize; the default '403 Forbidden'.
     *
     * @param ctx the current request context
     * @return a <code>403 Forbidden</code> result
     */
    public Result onUnauthorized(Http.Context ctx, String action) {
        return forbidden(Json.toJson(new HashMap<String, String>() {{
            put("Error", String.format("The current user is not allowed to perform this action: `%s`", action));
        }}));
    }
}
