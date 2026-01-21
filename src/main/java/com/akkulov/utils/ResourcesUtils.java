package com.akkulov.utils;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public final class ResourcesUtils {
    private ResourcesUtils() {
    }

    public static void closeResources(InitialContext ic, Connection connection) throws NamingException, JMSException {
        if (ic != null) {
            ic.close();
        }

        if (connection != null) {
            connection.close();
        }
    }
}
