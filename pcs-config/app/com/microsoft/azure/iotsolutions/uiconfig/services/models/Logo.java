// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import play.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;

public class Logo {

    private static final Logger.ALogger log = Logger.of(Logo.class);

    public static final Logo Default;
    public static final String IS_DEFAULT_HEADER = "IsDefault";
    public static final String NAME_HEADER = "Name";
    private static final String DEFAULT_LOGO_NAME = "Default Logo";
    private static final String SVG_TYPE = "image/svg+xml";
    private static final String DEFAULT_LOGO_PATH = "/resources/content/DefaultLogo.svg";


    static {
        Default = new Logo();
        Default.setType(Logo.SVG_TYPE);
        Default.setName(Logo.DEFAULT_LOGO_NAME);
        Default.setDefault(true);
        try {
            InputStream stream = Logo.class.getResourceAsStream(Logo.DEFAULT_LOGO_PATH);
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
    private String name;
    private boolean isDefault;

    public Logo() {
    }

    public Logo(String image, String type, String name, boolean isDefault) {
        this.image = image;
        this.type = type;
        this.name = name;
        this.isDefault = isDefault;
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

    @JsonProperty("Name")
    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    @JsonProperty("IsDefault")
    public boolean getDefault() { return this.isDefault; }

    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }
}
