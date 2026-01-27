package com.akkulov.jms_2_0;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Queue;
import javax.jms.StreamMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Отправка сообщений в виде мапы ключ-значение.
 */
public class V10MapMessageDemo {

    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext context = new InitialContext();
        var requestQueue = (Queue) context.lookup("queue/requestQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61620", "artemis", "artemis");
             var jmsContext = cf.createContext()
        ) {
            var producer = jmsContext.createProducer();

            var mapMessage = jmsContext.createMapMessage();
            mapMessage.setFloat("age", 26);
            mapMessage.setString("name", "Omar");

            producer.send(requestQueue, mapMessage);

            var consumer = jmsContext.createConsumer(requestQueue);
            var received = (MapMessage) consumer.receive();
            System.out.println("Received: " + received.getFloat("age"));
            System.out.println("Received: " + received.getString("name"));
        }
    }
}
