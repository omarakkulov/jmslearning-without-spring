package com.akkulov.jms_1_0;

import com.akkulov.utils.ResourcesUtils;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Первый вид JMS 1.0 до появления 2.0.
 */
public class V1MyQueue {
    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext ic = new InitialContext();
        Queue myQueue = (Queue) ic.lookup("queue/myQueue");

        ConnectionFactory cf = (ConnectionFactory) ic.lookup("ConnectionFactory");

        Connection connection = cf.createConnection("artemis", "artemis");
        var session = connection.createSession();

        var producer = session.createProducer(myQueue);

        var message = session.createTextMessage("OMAR");
        producer.send(message);
        System.out.println("Message sent: " + message.getText());

        var consumer = session.createConsumer(myQueue);
        //// чтение сообщения
        // запускаем этот метод, когда хотим читать сообщения из брокера(очереди), то есть мы говорим провайдеру JMS о том, что готовы
        // слушать сообщения
        connection.start();
        var messageReceived = (TextMessage) consumer.receive(5000);
        System.out.println("Message received: " + messageReceived.getText());

        ResourcesUtils.closeResources(ic, connection);
    }
}
