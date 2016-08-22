package org.damuzee.mongo;

import com.mongodb.DB;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.dao.DataAccessException;

public interface MongoFactory extends DisposableBean{


	/**
	 * Creates a default {@link MongoDatabase} instance.
	 *
	 *
	 */
	public MongoDatabase getDatabase();
}
