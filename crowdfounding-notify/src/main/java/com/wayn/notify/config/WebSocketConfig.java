package com.wayn.notify.config;

import com.wayn.notify.config.handshakehandler.MyPrincipalHandshakeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * websocket启用stomp消息配置
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    @Autowired
    private MyPrincipalHandshakeHandler myPrincipalHandshakeHandler;


    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        stompEndpointRegistry.setErrorHandler(new StompSubProtocolErrorHandler());
        stompEndpointRegistry.addEndpoint("/notify")
                // 配置拦截器，在拦截器中配置用户认证信息
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                // 配置握手处理器，websocket的用户认证信息从握手处理器获得
                .setHandshakeHandler(myPrincipalHandshakeHandler)
                // 配置websocket跨域
                .setAllowedOrigins("*")
                // 启用sockjs
                .withSockJS()
                // 设置连接时长
                .setDisconnectDelay(30 * 1000);
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setUserDestinationPrefix("/user/");
        registry.setApplicationDestinationPrefixes("/app");//走@messageMapping
//        registry.setPathMatcher(new AntPathMatcher()); // 设置路径匹配规则
    }

   // 通过configureClientInboundChannel方法进行用户认证处理
    /*@Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        super.configureClientInboundChannel(registration);
        registration.interceptors(new ChannelInterceptorAdapter() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    Principal user = new Principal() {
                        @Override
                        public String getName() {
                            return ShiroUtil.getSessionUser().getId();
                        }
                    }; // access authentication header(s)
                    accessor.setUser(user);
                }
                return message;
            }
        });
    }*/
}
