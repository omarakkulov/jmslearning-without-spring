package com.akkulov.jms_2_0;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class V3RequestReplyDemo {
    public static void main(String[] args) throws NamingException {
        InitialContext context = new InitialContext();
        var requestQueue = (Queue) context.lookup("queue/requestQueue");
        var responseQueue = (Queue) context.lookup("queue/responseQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61620", "artemis", "artemis");
             var jmsContext = cf.createContext()
        ) {
            var requestMessage = "Hello World";

            var requestProducer = jmsContext.createProducer();
            requestProducer.send(requestQueue, requestMessage);
            System.out.println("Message sent to request-queue: " + requestMessage);

            var requestQueueConsumer = jmsContext.createConsumer(requestQueue);
            var requestQueueReceivedMessage = requestQueueConsumer.receiveBody(String.class);
            System.out.println("Message received from request-queue: " + requestQueueReceivedMessage);

            var responseProducer = jmsContext.createProducer();
            responseProducer.send(responseQueue, "Response: " + requestMessage);
            System.out.println("Message sent to response-queue: " + "Response: " + requestMessage);

            var responseQueueConsumer = jmsContext.createConsumer(responseQueue);
            var responseQueueReceivedMessage = responseQueueConsumer.receiveBody(String.class);
            System.out.println("Message received from response-queue: " + responseQueueReceivedMessage);
        }
    }
}
