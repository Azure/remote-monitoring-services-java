// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.services.models;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LogoServiceModel {
    public static final LogoServiceModel Default;

    static {
        Default = new LogoServiceModel();
        Default.setType("image/svg+xml");
        try (InputStream stream = LogoServiceModel.class.getResourceAsStream("../content/DefaultLogo.svg")) {
            try (InputStreamReader in = new InputStreamReader(stream, "UTF-8")) {
                try (BufferedReader reader = new BufferedReader(in)) {
                    StringBuffer sb = new StringBuffer();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    Default.setImage(sb.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String image;
    private String type;

    public LogoServiceModel() {
    }

    public LogoServiceModel(String image, String type) {
        this.image = image;
        this.type = type;
    }

    public static LogoServiceModel getDefault() {
        return Default;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
