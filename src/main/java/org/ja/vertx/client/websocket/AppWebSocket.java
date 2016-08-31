package org.ja.vertx.client.websocket;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.JsonObject;
import org.apache.log4j.Logger;

/**
 * This class handles the web soccket connection to the angular client
 *
 * @author Andre Jacobs
 * Created by andre on 25.08.16.
 */
public class AppWebSocket {

    private Logger log = Logger.getLogger(AppWebSocket.class);

    private final int WSPORT = 8090;

    public AppWebSocket(Vertx vertx, EventBus eventBus) {
        startWebSocketServer(vertx, eventBus);
    }

    /**
     * open an websocket server and wating for requests
     */
    private void startWebSocketServer(Vertx vertx, EventBus eventBus) {

        vertx.createHttpServer().websocketHandler(new Handler<ServerWebSocket>() {

            @Override
            public void handle(ServerWebSocket ws) {
                final String id = ws.textHandlerID();
                log.info("ws event: " + ws.toString() +  " id: " + id);

                //closing of the websocket
                ws.closeHandler(new Handler<Void>() {

                    @Override
                    public void handle(Void event) {
                        log.info("the web socket has been closed");
                    }

                });

                //incomming requests from the client
                ws.handler(new Handler<Buffer>() {

                    @Override
                    public void handle(Buffer buffer) {
                        JsonObject json = new JsonObject();
                        json.put("data", "message from server");

                        ws.writeFinalTextFrame(json.encodePrettily());

                        JsonObject eventMessage = new JsonObject();
                        eventMessage.put("data", buffer.toString());
                        eventBus.send("test", eventMessage);
                    }
                });
            }

        }).listen(WSPORT);
        log.info("started websocket server");
    }
}
