package com.akkulov.jms_2_0;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Установка кастомных пропертей в отправляемое сообщение.
 */
public class V7MessagePropertiesDemo {
    public static void main(String[] args) throws NamingException, JMSException, InterruptedException {
        InitialContext context = new InitialContext();
        var requestQueue = (Queue) context.lookup("queue/requestQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61620", "artemis", "artemis");
             var jmsContext = cf.createContext()
        ) {
            var message = jmsContext.createTextMessage("REQUEST");
            //устанавливаем конфигурации сообщения
            message.setBooleanProperty("loggedIn", true);
            message.setStringProperty("username", "omarakkulov");

            var producer = jmsContext.createProducer();
            producer.send(requestQueue, message);
            System.out.println("Sent: " + message.getText());

            var consumer = jmsContext.createConsumer(requestQueue);
            var receivedMessage = (TextMessage) consumer.receive(3000);
            System.out.println("Received: " + receivedMessage);
            System.out.println("Custom properties 'loggedIn': " + receivedMessage.getBooleanProperty("loggedIn"));
            System.out.println("Custom properties 'username': " + receivedMessage.getStringProperty("username"));
        }
    }
}
