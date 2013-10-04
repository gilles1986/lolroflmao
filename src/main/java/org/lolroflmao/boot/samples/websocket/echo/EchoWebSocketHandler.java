package org.lolroflmao.boot.samples.websocket.echo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lolroflmao.beans.factory.annotation.Autowired;
import org.lolroflmao.web.socket.CloseStatus;
import org.lolroflmao.web.socket.TextMessage;
import org.lolroflmao.web.socket.WebSocketHandler;
import org.lolroflmao.web.socket.WebSocketSession;
import org.lolroflmao.web.socket.adapter.TextWebSocketHandlerAdapter;

/**
 * Echo messages by implementing a Spring {@link WebSocketHandler} abstraction.
 */
public class EchoWebSocketHandler extends TextWebSocketHandlerAdapter {

	private static Logger logger = LoggerFactory.getLogger(EchoWebSocketHandler.class);

	private final EchoService echoService;

	@Autowired
	public EchoWebSocketHandler(EchoService echoService) {
		this.echoService = echoService;
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		logger.debug("Opened new session in instance " + this);
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String echoMessage = this.echoService.getMessage(message.getPayload());
		logger.debug(echoMessage);
		session.sendMessage(new TextMessage(echoMessage));
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		session.close(CloseStatus.SERVER_ERROR);
	}

}
