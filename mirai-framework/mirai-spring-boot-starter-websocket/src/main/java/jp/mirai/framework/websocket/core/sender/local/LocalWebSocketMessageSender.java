package jp.mirai.framework.websocket.core.sender.local;

import jp.mirai.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import jp.mirai.framework.websocket.core.sender.WebSocketMessageSender;
import jp.mirai.framework.websocket.core.session.WebSocketSessionManager;

/**
 * 本地的 {@link WebSocketMessageSender} 实现类
 *
 * 注意：仅仅适合单机场景！！！
 *
 * @author mirai
 */
public class LocalWebSocketMessageSender extends AbstractWebSocketMessageSender {

    public LocalWebSocketMessageSender(WebSocketSessionManager sessionManager) {
        super(sessionManager);
    }

}
