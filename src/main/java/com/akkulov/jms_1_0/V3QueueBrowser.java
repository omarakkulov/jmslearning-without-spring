package com.akkulov.jms_1_0;

import com.akkulov.utils.ResourcesUtils;
import org.w3c.dom.Text;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Для просмотра содержимого конкретной очереди JMS.
 */
public class V3QueueBrowser {
    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext ic = new InitialContext();
        Queue myQueue = (Queue) ic.lookup("queue/myQueue");

        ConnectionFactory cf = (ConnectionFactory) ic.lookup("ConnectionFactory");
        Connection connection = cf.createConnection("artemis", "artemis");
        var session = connection.createSession();

        var producer = session.createProducer(myQueue);

        var message1 = session.createTextMessage("OMAR1");
        var message2 = session.createTextMessage("OMAR2");
        var message3 = session.createTextMessage("OMAR3");

        producer.send(message1);
        producer.send(message2);
        producer.send(message3);

        System.out.println(
                "Messages sent: \n" +
                        message1.getText() + "\n" +
                        message2.getText() + "\n" +
                        message3.getText() + "\n"
        );

        var browser = session.createBrowser(myQueue);

        var resultMessages = new ArrayList<TextMessage>();
        //мы просто вычитываем сообщения из очереди, не удаляем их из очереди jms, как при consumer.receive()
        var messages = browser.getEnumeration();
        while (messages.hasMoreElements()) {
            var jmsMessage = (TextMessage) messages.nextElement();
            resultMessages.add(jmsMessage);
        }

        var list = resultMessages.stream()
                .map(it -> {
                    try {
                        return it.getText();
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();

        System.out.println("Messages was opened and readed: " + list);

        ResourcesUtils.closeResources(ic, connection);
        /*
        //// чтение сообщения
        // запускаем этот метод, когда хотим читать сообщения из брокера(очереди), то есть мы говорим провайдеру JMS о том, что готовы
        // слушать сообщения
        connection.start();

        var messageReceived = (TextMessage) browser.receive(5000);
        System.out.println("Message received: " + messageReceived.getText());
         */
    }
}
