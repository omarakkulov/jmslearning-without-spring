package com.akkulov.jms_2_0;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * кейс когда producer шлет запрос в очередь и какой-то consumer его обрабатывает и необходимо получить ответ обратно точно тот, который
 * и нужно получить, без путанницы с другими ответами - messageId + correlationId
 */
public class V5RequestReplyDemo {
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

            /*
            типа отправитель
             */
            var sentJmsMessageID = requestTextMessage.getJMSMessageID();
            System.out.println("Message sent to request-queue, JmsMessageId={" + sentJmsMessageID + "}, replyTo={" + requestTextMessage.getJMSReplyTo().toString() +
                    "}: " + requestTextMessage.getText());

            /*
              типа какой-то внешний consumerX принял сообщение из очереди requestQueue
             */
            var requestQueueConsumer = jmsContext.createConsumer(requestQueue);
            var requestQueueReceivedMessage = (TextMessage) requestQueueConsumer.receive();
            // смотрим на getJMSMessageID() и getJMSReplyTo()
            var replyTo = requestQueueReceivedMessage.getJMSReplyTo();// очередь для ответов
            var correlationId = requestQueueReceivedMessage.getJMSMessageID();
            System.out.println("Message received from request-queue, CorrelationId={" + correlationId + "}, replyTo={" + replyTo.toString() + "}: " + requestQueueReceivedMessage.getText());

            /*
            этот же внешний сервис consumerX шлет запрос обратно в очередь replyTo
             */
            var responseTextMessage = jmsContext.createTextMessage("RESPONSE");
            responseTextMessage.setJMSCorrelationID(correlationId);
            var responseQueueProducer = jmsContext.createProducer();
            // отправляем сообщение в очередь ответов через getJMSReplyTo()
            responseQueueProducer.send(replyTo, responseTextMessage);
            System.out.println("Message sent to response-queue: CorrelationId={" + correlationId + "}, replyTo={" + replyTo + "}" + responseTextMessage.getText());

            /*
            типа отправитель достает ответ обратно
             */
            // вытаскиваем сообщения из очереди ответов через getJMSReplyTo()
            var responseSelector = "JMSCorrelationID = '" + sentJmsMessageID + "'";
            var responseQueueConsumer = jmsContext.createConsumer(requestTextMessage.getJMSReplyTo(), responseSelector);
            var responseQueueReceivedMessage = (TextMessage) responseQueueConsumer.receive();
            System.out.println("Message received from response-queue: " + responseQueueReceivedMessage.getText());

            context.close();
        }
    }
}
