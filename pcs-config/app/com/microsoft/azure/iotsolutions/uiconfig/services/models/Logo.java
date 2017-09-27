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

    static {
        Default = new Logo();
        Default.setType("image/svg+xml");
        try {
            InputStream stream = Logo.class.getResourceAsStream("../content/DefaultLogo.svg");
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
            // Because travis test can not read file by now for some reason, Java.lang.Class can not read resource
            // in docker container. we use a work around to return fallback
            // Base64 string encoded from the default logo file.
            Default.setImage(
                    "PCEtLSBDb3B5cmlnaHQgKGMpIE1pY3Jvc29mdC4gQWxsIHJpZ2h0cyByZXNl"
                            + "cnZlZC4gLS0+PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9z"
                            + "dmciIHZpZXdCb3g9IjAgMCA3LjE3MiA3LjQ0MSI+PHRpdGxlPkFzc2V0IDg8"
                            + "L3RpdGxlPjxnIGlkPSJMYXllcl8yIiBkYXRhLW5hbWU9IkxheWVyIDIiPjxn"
                            + "IGlkPSJMYXllcl8xLTIiIGRhdGEtbmFtZT0iTGF5ZXIgMSI+PHBhdGggZD0i"
                            + "TS41MTIsNi45MjhoNi42NnYuNTEySDBWMEguNTEyWk0yLjU2MiwyLjYxVjYu"
                            + "NDE2SDEuMDI1VjIuNjFaTTIuMDQ5LDUuOVYzLjEyM0gxLjUzN1Y1LjlaTTQu"
                            + "NjExLjk1MVY2LjQxNkgzLjA3NFYuOTUxWk00LjEsNS45VjEuNDYzSDMuNTg2"
                            + "VjUuOVpNNi42NiwxLjk4OFY2LjQxNkg1LjEyM1YxLjk4OFpNNi4xNDgsNS45"
                            + "VjIuNUg1LjYzNVY1LjlaIiBmaWxsPSIjYWZiOWMzIi8+PC9nPjwvZz48L3N2"
                            + "Zz4=");
            log.warn("Unable to load default logo resource, use fallback Base64 string.");
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
