package org.damuzee.mongo;

/**
 * Created by karka.w on 2015/8/29.
 */
public class MongoException extends Exception {
    public MongoException() {
        super();
    }

    public MongoException(String message) {
        super(message);
    }

    public MongoException(String message, Throwable cause) {
        super(message, cause);
    }

    public MongoException(Throwable cause) {
        super(cause);
    }

    protected MongoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
