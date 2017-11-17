// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.iotsolutions.uiconfig.services.exceptions.ResourceNotFoundException;
import play.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

public class Logo {

    private static final Logger.ALogger log = Logger.of(Logo.class);

    public static final Logo Default;

    static {
        Default = new Logo();
        Default.setType("image/svg+xml");
        try {
            InputStream stream = Logo.class.getResourceAsStream("/resources/content/DefaultLogo.svg");
            InputStreamReader in = new InputStreamReader(stream, "UTF-8");
            BufferedReader reader = new BufferedReader(in);
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String base64String = Base64.getEncoder().encodeToString(sb.toString().getBytes());
            Default.setImage(base64String);
        } catch (Exception e) {
            log.error("Unable to load default logo resource");
        }
    }

    private String image;
    private String type;

    public Logo() {
    }

    public Logo(String image, String type) {
        this.image = image;
        this.type = type;
    }

    @JsonProperty("Default")
    public static Logo getDefault() {
        return Default;
    }

    @JsonProperty("Image")
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @JsonProperty("Type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
