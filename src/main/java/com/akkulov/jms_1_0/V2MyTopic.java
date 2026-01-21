package com.akkulov.jms_1_0;

import com.akkulov.utils.ResourcesUtils;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class V2MyTopic {
    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext ic = new InitialContext();
        Topic topic = (Topic) ic.lookup("topic/myTopic");

        ConnectionFactory cf = (ConnectionFactory) ic.lookup("ConnectionFactory");
        Connection connection = cf.createConnection("artemis", "artemis");

        var session = connection.createSession();
        var producer = session.createProducer(topic);

        var consumer1 = session.createConsumer(topic);
        var consumer2 = session.createConsumer(topic);

        var message = session.createTextMessage("OMAR");
        producer.send(message);
        System.out.println("Message sent: " + message.getText());

        //// чтение сообщения
        // запускаем этот метод, когда хотим читать сообщения из брокера(очереди), то есть мы говорим провайдеру JMS о том, что готовы
        // слушать сообщения
        connection.start();

        var messageReceived1 = (TextMessage) consumer1.receive(5000);
        System.out.println("Consumer1 message received: " + messageReceived1.getText());

        var messageReceived2 = (TextMessage) consumer2.receive(5000);
        System.out.println("Consumer2 message received: " + messageReceived2.getText());

        ResourcesUtils.closeResources(ic, connection);
    }
}
