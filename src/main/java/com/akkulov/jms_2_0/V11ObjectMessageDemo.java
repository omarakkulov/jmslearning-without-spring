package com.akkulov.jms_2_0;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;

/**
 * Отправка сообщений в виде Object.
 */
public class V11ObjectMessageDemo {

    public static void main(String[] args) throws NamingException, JMSException {
        InitialContext context = new InitialContext();
        var requestQueue = (Queue) context.lookup("queue/requestQueue");

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61620", "artemis", "artemis");
             var jmsContext = cf.createContext()
        ) {
            var producer = jmsContext.createProducer();

            var objectMessage = jmsContext.createObjectMessage();
            objectMessage.setObject(
                    Employee.builder()
                            .age(20)
                            .name("Omar")
                            .build()
            );

            producer.send(requestQueue, objectMessage);

            var consumer = jmsContext.createConsumer(requestQueue);
            var received = (ObjectMessage) consumer.receive();
            System.out.println("Received: " + received.getObject());
        }
    }

    static class Employee implements Serializable {

        private String name;
        private int age;

        public Employee(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "Employee{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }

        private static class Builder {

            private String name;
            private int age;

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder age(int age) {
                this.age = age;
                return this;
            }

            public Employee build() {
                return new Employee(name, age);
            }
        }
    }
}
