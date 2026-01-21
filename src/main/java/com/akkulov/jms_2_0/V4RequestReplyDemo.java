package com.akkulov.jms_2_0;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Запрос - ответ используя "replyTo" header.
 */
public class V4RequestReplyDemo {
    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext context = new InitialContext();
        var requestQueue = (Queue) context.lookup("queue/requestQueue");
        var responseQueue = (Queue) context.lookup("queue/responseQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61620", "artemis", "artemis");
             var jmsContext = cf.createContext()
        ) {
            var requestTextMessage = jmsContext.createTextMessage("Hello World");
            // вставляем хэдер replyTo и в значении будет та очередь, в которую хотим получить ответ
            requestTextMessage.setJMSReplyTo(responseQueue);

            var requestQueueProducer = jmsContext.createProducer();
            requestQueueProducer.send(requestQueue, requestTextMessage);
            System.out.println("Message sent to request-queue, replyTo={" + requestTextMessage.getJMSReplyTo().toString() + "}: " + requestTextMessage.getText());

            var requestQueueConsumer = jmsContext.createConsumer(requestQueue);
            var requestQueueReceivedMessage = (TextMessage) requestQueueConsumer.receive();
            System.out.println("Message received from request-queue, replyTo={" + requestQueueReceivedMessage.getJMSReplyTo().toString() + "}: " + requestQueueReceivedMessage.getText());

            var responseQueueProducer = jmsContext.createProducer();
            // отправляем сообщение в очередь ответов через getJMSReplyTo()
            responseQueueProducer.send(requestQueueReceivedMessage.getJMSReplyTo(), requestQueueReceivedMessage.getText());
            System.out.println("Message sent to response-queue:, replyTo={" + requestQueueReceivedMessage.getJMSReplyTo().toString() + "}" + requestQueueReceivedMessage.getText());

            // вытаскиваем сообщения из очереди ответов через getJMSReplyTo()
            var responseQueueConsumer = jmsContext.createConsumer(requestQueueReceivedMessage.getJMSReplyTo());
            var responseQueueReceivedMessage = (TextMessage) responseQueueConsumer.receive();
            System.out.println("Message received from response-queue: " + responseQueueReceivedMessage.getText());

            context.close();
        }
    }
}
