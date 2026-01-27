package com.akkulov.jms_2_0;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Задаем приоритет отправки сообщений и убеждаемся, что те, что с приоритетом выше, отправятся брокеру первее.
 */
public class V2MessagePriority {
    public static void main(String[] args) throws NamingException {
        InitialContext context = new InitialContext();
        var queue = (Queue) context.lookup("queue/myQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61620", "artemis", "artemis");
             var jmsContext = cf.createContext()
        ) {
            var producer = jmsContext.createProducer();

            var messages = new String[3];
            messages[0] = "Priority 3";
            messages[1] = "Priority 9";
            messages[2] = "Priority 1";

            producer.setPriority(3);
            producer.send(queue, messages[0]);

            producer.setPriority(9);
            producer.send(queue, messages[1]);

            producer.setPriority(1);
            producer.send(queue, messages[2]);

            var consumer = jmsContext.createConsumer(queue);
            for (int i = 0; i < messages.length; i++) {
                System.out.println("Received message: " + consumer.receiveBody(String.class));
            }
        }
    }
}
