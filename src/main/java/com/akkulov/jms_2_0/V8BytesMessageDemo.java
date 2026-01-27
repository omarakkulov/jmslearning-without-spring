package com.akkulov.jms_2_0;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Отправка сообщений в виде байтового представления.
 */
public class V8BytesMessageDemo {

    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext context = new InitialContext();
        var requestQueue = (Queue) context.lookup("queue/requestQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61620", "artemis", "artemis");
             var jmsContext = cf.createContext()
        ) {
            var producer = jmsContext.createProducer();

            var bytesMessage = jmsContext.createBytesMessage();
            bytesMessage.writeUTF("OMAR ZVER");
            bytesMessage.writeLong(123);

            producer.send(requestQueue, bytesMessage);

            var consumer = jmsContext.createConsumer(requestQueue);
            var received = (BytesMessage) consumer.receive();
            System.out.println("Received: " + received.readUTF());
            System.out.println("Received: " + received.readLong());
        }
    }
}
