package org.ja.vertx.client;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.apache.log4j.Logger;
import org.ja.vertx.client.runner.Runner;
import org.ja.vertx.client.websocket.AppWebSocket;


/**
 * Simple vertx server that serve a angular ui and communicate
 * via rest and web socket.
 *
 * @author Andre Jacobs
 * <p>
 * Created by andre on 25.08.16.
 */
public class VertxClient extends AbstractVerticle {

    private Logger log = Logger.getLogger(VertxClient.class);

    private final int PORT = 8080;
    private final int RESTPORT = 8085;

    private EventBus eventBus;
    private HttpClient client;

    private JsonObject result = null;

    public static void main(String... args) {
        Runner.runExample(VertxClient.class);
    }

    @Override
    public void start() {
        AppWebSocket appWebSocket = new AppWebSocket(getVertx(), getEventBus());

        startRouter();
        registerConsumer();

    }

    /**
     * Get a httpClient
     *
     * @return A vertx HttpClient
     */
    private HttpClient getHttpClient() {
        if (client == null) {
            client = vertx.createHttpClient();
        }
        return client;
    }

    /**
     * Create Eventbus
     *
     * @throws Exception
     */
    private EventBus getEventBus() {
        try {
            if (eventBus == null) {
                eventBus = getVertx().eventBus();
            }
        } catch (Exception ex) {
            log.error("Error during creating of vertx eventbus ", ex);
        }
        return eventBus;
    }

    /**
     * Register an consumer on eventbus
     */
    private void registerConsumer() {

        getEventBus().consumer("test", new Handler<Message<JsonObject>>() {

            @Override
            public void handle(Message<JsonObject> m) {
                JsonObject received = m.body();
                String value = received.getString("data");
                log.info("message received: " + m.body() + " value " + value);
            }

        });
    }


    /**
     * server requests for rest and other, that depends on the request mapping
     */
    private void startRouter() {

        Vertx vertx = getVertx();
        Router router = Router.router(vertx);

        //cors
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedHeader("Content-Type"));

        router.route().handler(BodyHandler.create());
        router.get("/rest/*").handler(this::handleGet);
        router.route("/*").handler(this::handle);

        vertx.createHttpServer().requestHandler(router::accept).listen(PORT);
        log.info("vertx server started and listen on port " + PORT);
    }

    /**
     * Handle rest request
     *
     * @param routingContext
     */
    private void handleGet(RoutingContext routingContext) {
        HttpServerResponse response = routingContext.response();
        JsonObject json = new JsonObject();
        json.put("name", "josh");
        response.putHeader("content-type", "application/json").end(json.encodePrettily());
    }

    /**
     * Hanndle request with mapping /*. in this case the pages etc
     *
     * @param routingContext
     */
    private void handle(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        String file = req.path().equals("/") ? "index.html" : req.path();
        req.response().sendFile("webclient/" + file);
    }

    /**
     * Example how to call another rest resource
     */
    private void callOtherServer() {
        client.getNow(RESTPORT, "localhost", "/rest/name", httpClientResponse -> {

            httpClientResponse.bodyHandler(handlerBuffer -> {
                log.info("response from server " + handlerBuffer.getString(0, handlerBuffer.length()));
            });
        });
    }

}
