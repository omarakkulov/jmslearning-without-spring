package com.akkulov.jms_2_0;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Шлем сообщение в очередь и читаем оттуда же.
 */
public class V1JmsContextDemo {
    public static void main(String[] args) throws NamingException {
        InitialContext context = new InitialContext();
        var queue = (Queue) context.lookup("queue/myQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61620", "artemis", "artemis");
             var jmsContext = cf.createContext()
        ) {
            var message = "Hello World";
            jmsContext.createProducer().send(queue, message);
            System.out.println("Message sent: " + message);

            var receivedMessage = jmsContext.createConsumer(queue).receiveBody(String.class);
            System.out.println("Message received: " + receivedMessage);
        }
    }
}
