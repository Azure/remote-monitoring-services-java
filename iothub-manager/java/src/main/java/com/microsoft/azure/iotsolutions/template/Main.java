// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iotsolutions.template;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.AllDirectives;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.microsoft.azure.iotsolutions.template.runtime.Config;
import com.microsoft.azure.iotsolutions.template.runtime.IConfig;
import com.microsoft.azure.iotsolutions.template.runtime.InjectionModule;

import java.util.Scanner;
import java.util.concurrent.CompletionStage;

public class Main extends AllDirectives {
    // Init DI
    static Injector injector = Guice.createInjector(new InjectionModule());
    static IConfig config = injector.getInstance(Config.class);

    // Service components
    static ActorSystem system;
    static Http http;
    static CompletionStage<ServerBinding> binding;

    public static void main(String[] args) throws Exception {
        setup();

        int port = config.getWebServicePort();
        System.out.println("Server listening at " + config.getHostname() + ":" + config.getWebServicePort() + " - e.g. try");
        System.out.println("GET http://127.0.0.1:" + port + "/devices/102354");
        System.out.println("GET http://127.0.0.1:" + port + "/devices/badId");
        System.out.println("POST http://127.0.0.1:" + port + "/buildings  {\"devices\":[{\"id\":1, \"name\":\"foo\"}, {\"id\":2, \"name\":\"bar\"}]}");
        waitForEnter();

        teardown();
    }

    /**
     * Setup the actor system and start the web service.
     */
    static void setup() {
        // Create the actor system that will manage the incoming requests.
        system = ActorSystem.create("routes");
        ActorMaterializer materializer = ActorMaterializer.create(system);

        // In order to access all directives we need an instance where the routes are defined.
        WebService app = injector.getInstance(WebService.class);
        Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = app.setupRoutes().flow(system, materializer);

        // Boot up web service using the routes defined in `WebService`
        http = Http.get(system);
        binding = http.bindAndHandle(routeFlow, ConnectHttp.toHost(config.getHostname(), config.getWebServicePort()), materializer);
    }

    /**
     * Clean shutdown.
     */
    static void teardown() {
        binding
            .thenCompose(ServerBinding::unbind) // trigger unbinding from the port
            .thenAccept(unbound -> system.terminate()); // and shutdown when done
    }

    static void waitForEnter() {
        System.out.println("Press ENTER to stop...");
        (new Scanner(System.in)).nextLine();
    }
}
