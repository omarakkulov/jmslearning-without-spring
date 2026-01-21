package com.akkulov.jms_2_0;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Как установить срок действия эвентам(сообщениям) используя setTimToLive.
 */
public class V6MessageExpirationDemo {
    public static void main(String[] args) throws NamingException, JMSException, InterruptedException {
        InitialContext context = new InitialContext();
        var requestQueue = (Queue) context.lookup("queue/requestQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61620", "artemis", "artemis");
             var jmsContext = cf.createContext()
        ) {
            var requestMessage = "REQUEST";

            var producer = jmsContext.createProducer();
            // устанавливаем срок действия для каждого сообщения, отправляемого из этого продюсера
            producer.setTimeToLive(2000);
            producer.send(requestQueue, requestMessage);
            System.out.println("Sent: " + requestMessage);

            // имитируем какую-то деятельность, чтобы сообщение истекло
            Thread.sleep(5000);

            var consumer = jmsContext.createConsumer(requestQueue);
            var receivedMessage = (TextMessage) consumer.receive(3000);
            System.out.println("Received: " + receivedMessage);
        }
    }
}