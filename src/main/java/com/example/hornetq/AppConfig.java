package com.example.hornetq;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.netty.NettyAcceptorFactory;
import org.hornetq.core.remoting.impl.netty.TransportConstants;
import org.hornetq.core.server.JournalType;
import org.hornetq.jms.server.config.ConnectionFactoryConfiguration;
import org.hornetq.jms.server.config.JMSConfiguration;
import org.hornetq.jms.server.config.TopicConfiguration;
import org.hornetq.jms.server.config.impl.ConnectionFactoryConfigurationImpl;
import org.hornetq.jms.server.config.impl.JMSConfigurationImpl;
import org.hornetq.jms.server.config.impl.TopicConfigurationImpl;
import org.hornetq.jms.server.embedded.EmbeddedJMS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
	@Bean( initMethod = "start", destroyMethod = "stop" )
	public EmbeddedJMS broker() throws Exception {
		final ConfigurationImpl configuration = new ConfigurationImpl();
		configuration.setPersistenceEnabled( false );
		configuration.setJournalType( JournalType.NIO );
		configuration.setJMXManagementEnabled( true );
		configuration.setSecurityEnabled( false );
		
		final Map< String, Object > params = new HashMap<>();
		params.put( TransportConstants.HOST_PROP_NAME, "localhost" );
		params.put( TransportConstants.PROTOCOL_PROP_NAME, "stomp_ws" );
		params.put( TransportConstants.PORT_PROP_NAME, "61614" );
		
		final TransportConfiguration stomp = new TransportConfiguration( NettyAcceptorFactory.class.getName(), params );
		configuration.getAcceptorConfigurations().add( stomp );
		configuration.getConnectorConfigurations().put( "stomp_ws", stomp );
		
		final ConnectionFactoryConfiguration cfConfig = new ConnectionFactoryConfigurationImpl( "cf", true, "/cf" );
		cfConfig.setConnectorNames( Collections.singletonList( "stomp_ws" ) );
		
		final JMSConfiguration jmsConfig = new JMSConfigurationImpl();
		jmsConfig.getConnectionFactoryConfigurations().add( cfConfig );
		
		final TopicConfiguration topicConfig = new TopicConfigurationImpl( "test", "/topic/test" );
		jmsConfig.getTopicConfigurations().add( topicConfig );
		
		final EmbeddedJMS jmsServer = new EmbeddedJMS();
		jmsServer.setConfiguration( configuration );
		jmsServer.setJmsConfiguration( jmsConfig );
		
		return jmsServer;
	}
}
