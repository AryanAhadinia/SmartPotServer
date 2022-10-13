package server.live;

import io.javalin.websocket.WsContext;
import model.User;

import java.util.HashMap;
import java.util.HashSet;

public class LiveServer {
    private final HashMap<String, WsContext> usersBroadcasts;

    public LiveServer() {
        this.usersBroadcasts = new HashMap<>();
    }

    public void subscribe(User user, WsContext ws) {
        usersBroadcasts.put(user.getEmail(), ws);
    }

    public void broadcast(String message, User user) {
        WsContext wsContext = usersBroadcasts.get(user.getEmail());
        if (wsContext != null && wsContext.session.isOpen()) {
            wsContext.send(message);
        }
    }
}
