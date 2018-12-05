package uk.co.egao.repl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;

@ServerEndpoint(value = "/repl")
public class WebSocketRepl {
    private static final Logger LOGGER =
            Logger.getLogger(WebSocketRepl.class.getName());

    private final Map<String, JShell> repls = new HashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        LOGGER.log(Level.INFO, "New connection with client: {0}",
                session.getId());
        repls.put(session.getId(), JShell.builder().build());
    }

    @OnMessage
    public String onMessage(String message, Session session) {
        LOGGER.log(Level.INFO, "New message from Client [{0}]: {1}",
                new Object[]{session.getId(), message});
        JShell repl = repls.get(session.getId());

        List<SnippetEvent> results = repl.eval(message);
        StringBuilder out = new StringBuilder("Server received [" + message + "]\n");
        for (var event : results) {
            out.append(event.toString()).append("\n");
        }
        return out.toString();
    }

    @OnClose
    public void onClose(Session session) {
        LOGGER.log(Level.INFO, "Close connection for client: {0}",
                session.getId());
        repls.remove(session.getId()).close();
    }

    @OnError
    public void onError(Throwable exception, Session session) {
        LOGGER.log(Level.INFO, "Error for client: {0}", session.getId());
    }
}
