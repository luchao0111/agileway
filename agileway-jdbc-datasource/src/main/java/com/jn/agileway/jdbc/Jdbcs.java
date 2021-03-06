package com.jn.agileway.jdbc;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Locale;

public class Jdbcs {
    /**
     * A constant for SQL Server's Snapshot isolation level
     */
    private static final int SQL_SERVER_SNAPSHOT_ISOLATION_LEVEL = 4096;
    /**
     * Get the int value of a transaction isolation level by name.
     *
     * @param transactionIsolationName the name of the transaction isolation level
     * @return the int value of the isolation level or -1
     */
    public static int getTransactionIsolation(final String transactionIsolationName)
    {
        if (transactionIsolationName != null) {
            try {
                // use the english locale to avoid the infamous turkish locale bug
                final String upperName = transactionIsolationName.toUpperCase(Locale.ENGLISH);
                if (upperName.startsWith("TRANSACTION_")) {
                    Field field = Connection.class.getField(upperName);
                    return field.getInt(null);
                }
                final int level = Integer.parseInt(transactionIsolationName);
                switch (level) {
                    case Connection.TRANSACTION_READ_UNCOMMITTED:
                    case Connection.TRANSACTION_READ_COMMITTED:
                    case Connection.TRANSACTION_REPEATABLE_READ:
                    case Connection.TRANSACTION_SERIALIZABLE:
                    case Connection.TRANSACTION_NONE:
                    case SQL_SERVER_SNAPSHOT_ISOLATION_LEVEL: // a specific isolation level for SQL server only
                        return level;
                    default:
                        throw new IllegalArgumentException();
                }
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Invalid transaction isolation value: " + transactionIsolationName);
            }
        }

        return -1;
    }
}
