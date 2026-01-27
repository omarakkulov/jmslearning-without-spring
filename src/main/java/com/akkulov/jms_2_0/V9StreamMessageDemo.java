package com.akkulov.jms_2_0;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.StreamMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Отправка сообщений в виде потока данных (stream).
 */
public class V9StreamMessageDemo {

    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext context = new InitialContext();
        var requestQueue = (Queue) context.lookup("queue/requestQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61620", "artemis", "artemis");
             var jmsContext = cf.createContext()
        ) {
            var producer = jmsContext.createProducer();

            var streamMessage = jmsContext.createStreamMessage();
            streamMessage.writeBoolean(true);
            streamMessage.writeFloat(5.180f);

            producer.send(requestQueue, streamMessage);

            var consumer = jmsContext.createConsumer(requestQueue);
            var received = (StreamMessage) consumer.receive();
            System.out.println("Received: " + received.readBoolean());
            System.out.println("Received: " + received.readFloat());
        }
    }
}
