// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.uiconfig.webservice.auth;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.nimbusds.jose.*;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import play.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.*;

/**
 * TODO: ensure certs are cached for a reasonable time
 *       https://github.com/Azure/iothub-manager-java/issues/51
 */
@Singleton
public class OpenIdConnectJwtValidation implements IJwtValidation {

    private static final Logger.ALogger log = Logger.of(OpenIdConnectJwtValidation.class);

    private final IClientAuthConfig config;

    private boolean setupComplete;

    /**
     * A set of JWT processors, one for each trusted signing algorithm
     */
    private HashMap<String, DefaultJWTProcessor> jwtProcessors;

    /**
     * The list of trusted algorithms (from the configuration)
     */
    private final HashSet<String> signingAlgos;

    /**
     * The expected token issuer (from the configuration)
     */
    private final String issuer;

    /**
     * The expected token audience  (from the configuration)
     */
    private final String audience;

    @Inject
    public OpenIdConnectJwtValidation(IClientAuthConfig config)
        throws InvalidConfigurationException, ExternalDependencyException {

        this.issuer = config.getJwtIssuer().toLowerCase();
        this.audience = config.getJwtAudience();
        this.signingAlgos = config.getJwtAllowedAlgos();
        this.config = config;

        // Note, the setup cannot throw exceptions or DI won't complete
        this.setupComplete = false;
        this.trySetup(false);
    }

    /**
     * Validate the JWT token:
     * - signature, using OpenId Connect Provider certs
     * - signing algorithm
     * - token lifetime
     * - expected issuer
     * - expected audience
     */
    public Boolean validateToken(String token)
        throws InvalidConfigurationException, ExternalDependencyException {

        this.trySetup(true);

        // Parse the token, we need this to know the signing algo and to decide
        // which processor to use
        JWSObject jwsToken;
        try {
            jwsToken = JWSObject.parse(token);
        } catch (java.text.ParseException e) {
            log.error("The authorization token is not valid");
            return false;
        }

        // Check whether the signing algorithm is allowed (from the configuration)
        String algo = jwsToken.getHeader().getAlgorithm().getName().toUpperCase();
        if (!this.validateSigningAlgo(algo)) {
            return false;
        }

        // Inspect the rest of the token
        DefaultJWTProcessor jwtProcessor = this.jwtProcessors.get(algo);
        SecurityContext ctx = null;
        try {
            // Check signature and lifetime, exception will trigger if something is off
            JWTClaimsSet claims = jwtProcessor.process(token, ctx);

            // Check issuer and audience
            return this.validateTokenIssuer(claims) && this.validateTokenAudience(claims);
        } catch (java.text.ParseException e) {
            log.error("Unable to parse the authorization token", e);
        } catch (BadJOSEException e) {
            // Bad JSON Object Signing and Encryption (JOSE) exception
            log.error("The authorization token signature is not valid", e);
        } catch (JOSEException e) {
            // Javascript Object Signing and Encryption (JOSE) exception
            log.error("Unable to process the authorization token signature", e);
        }

        return false;
    }

    /**
     * Try to setup the Open Id authentication classes, including downloading
     * the certificates used to verify JWT signatures. The call could fail
     * so it should be retried if that happens.
     *
     * The method can be called from the constructor, but in that case
     * exceptions should not be thrown, to allow Guice DI to complete
     * the object provisioning.
     */
    private void trySetup(Boolean throwOnError)
        throws InvalidConfigurationException, ExternalDependencyException {

        if (this.setupComplete) return;

        try {
            log.info("Configuring OpenId Connect");
            this.setupProcessors((int) this.config.getJwtClockSkew().getSeconds());
            this.setupComplete = true;
        } catch (Exception e) {
            log.error("Setup failed", e);
            this.setupComplete = false;

            if (throwOnError) {
                throw e;
            }
        }
    }

    /**
     * Ensure the token is signed with a trusted algorithm
     */
    private Boolean validateSigningAlgo(String algo) {

        if (this.signingAlgos.contains(algo.toUpperCase())) {
            return true;
        }

        log.error("The authorization token is signed with an invalid algorithm: {}", algo);
        return false;
    }

    /**
     * Check whether the token has been released by the expected issuer
     */
    private Boolean validateTokenIssuer(JWTClaimsSet claims) {

        String issuer = claims.getIssuer();
        if (issuer == null) {
            log.error("The authorization token doesn't have an issuer (iss)");
            return false;
        }

        if (issuer.toLowerCase().equals(this.issuer)) {
            return true;
        }

        log.error("The authorization token issuer `{}` doesn't match the expected issuer `{}`",
            issuer, this.issuer);

        return false;
    }

    /**
     * Check whether the token has been released to the expected audience
     */
    private boolean validateTokenAudience(JWTClaimsSet claims) {
        List<String> audiences = claims.getAudience();

        if (audiences == null) {
            log.error("The authorization token doesn't have an audience (aud)");
            return false;
        }

        if (audiences.contains(this.audience)) {
            return true;
        }

        log.error("The authorization token audience `{}` doesn't match the expected audience `{}`",
            audiences, this.audience);

        return false;
    }

    /**
     * Prepare the tokens processors, one per allowed signing algorithm
     */
    private void setupProcessors(int allowedClockSkew)
        throws InvalidConfigurationException, ExternalDependencyException {

        JWKSource keySource = this.getJwkSource();

        this.jwtProcessors = new HashMap<>();
        for (String s : this.signingAlgos) {
            // Create JWT processor
            DefaultJWTProcessor processor = new DefaultJWTProcessor();

            // Override the default instance (same class) in order to set our Clock skew value (the internal default is 60 seconds)
            DefaultJWTClaimsVerifier<SecurityContext> claimsVerifier = new DefaultJWTClaimsVerifier<>();
            claimsVerifier.setMaxClockSkew(allowedClockSkew);
            processor.setJWTClaimsSetVerifier(claimsVerifier);

            // Set the key selector
            processor.setJWSKeySelector(new JWSVerificationKeySelector(getAlgo(s), keySource));

            // Store the processor in the list, ready to be used
            this.jwtProcessors.put(s, processor);
        }
    }

    /**
     * Download the OpenId Connect provider metadata
     *
     * See: https://connect2id.com/products/nimbus-oauth-openid-connect-sdk/guides/java-cookbook-for-openid-connect-public-clients
     */
    private JWKSource getJwkSource()
        throws InvalidConfigurationException, ExternalDependencyException {

        URL providerConfigurationURL;
        try {
            providerConfigurationURL = new URI(this.issuer + ".well-known/openid-configuration").toURL();
        } catch (MalformedURLException e) {
            throw new InvalidConfigurationException("Invalid Issuer URL", e);
        } catch (URISyntaxException e) {
            throw new InvalidConfigurationException("Invalid Issuer URL", e);
        }

        InputStream stream;
        try {
            log.debug("Downloading OpenId Connect metadata");
            stream = providerConfigurationURL.openStream();
        } catch (IOException e) {
            throw new ExternalDependencyException("Unable to download OpenId Connect metadata", e);
        }

        String providerInfo;
        try (java.util.Scanner s1 = new java.util.Scanner(stream)) {
            providerInfo = s1.useDelimiter("\\A").hasNext() ? s1.next() : "";
        }

        OIDCProviderMetadata providerMetadata;
        try {
            log.debug("Parsing OpenId Connect metadata");
            providerMetadata = OIDCProviderMetadata.parse(providerInfo);
        } catch (ParseException e) {
            throw new ExternalDependencyException("Unable to parse OpenId Connect metadata", e);
        }

        try {
            log.debug("Instantiating a Remote JWK set, which will download the signing certificates to verify the tokens");
            return new RemoteJWKSet(providerMetadata.getJWKSetURI().toURL());
        } catch (MalformedURLException e) {
            throw new ExternalDependencyException("Invalid JWK Set URI returned by the OpenId Connect provider", e);
        }
    }

    private JWSAlgorithm getAlgo(String s)
        throws InvalidConfigurationException {

        switch (s) {
            case "RS256":
                return JWSAlgorithm.RS256;
            case "RS384":
                return JWSAlgorithm.RS384;
            case "RS512":
                return JWSAlgorithm.RS512;
            default:
                throw new InvalidConfigurationException("Unsupported algorithm " + s);
        }
    }
}
