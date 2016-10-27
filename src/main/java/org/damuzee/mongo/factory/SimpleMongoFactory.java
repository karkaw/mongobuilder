package org.damuzee.mongo.factory;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.damuzee.mongo.MongoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单的
 */
public class SimpleMongoFactory implements  MongoFactory {

	private static final Logger logger = LoggerFactory.getLogger(SimpleMongoFactory.class);

	private String dbName;
	private String host;
	private int port;
	private String username;
	private String password;
	private Boolean authentication ;

    private Mongo mongo = null ;
	private MongoDatabase db ;

	public MongoDatabase getDatabase(){
		logger.info("Mongodb db info ,db {} , host {} , port {} ", dbName, host, port);
		if(db == null){
			MongoClient client = null ;
			ServerAddress serverAddress = new ServerAddress(host,port);
			List<ServerAddress> seeds = new ArrayList();
			seeds.add(serverAddress);
			//用户认证
			if(authentication) {
				logger.info("Mongodb auth username {} , password {}, db {}",username,password,dbName);

				MongoCredential credentials = MongoCredential.createScramSha1Credential(username, dbName, password.toCharArray());
				List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();
				credentialsList.add(credentials);
				client = new MongoClient(seeds, credentialsList);
			}else {
				client = new MongoClient(seeds);
			}
			db = client.getDatabase(dbName);
		}
		return  db;
	}

	/**
	 * 销毁容器spring关闭mongo链接
	 * @throws Exception
	 */
	public void destroy() throws Exception {
		logger.info("Mongodb db closeing ...........");

		if(mongo != null){
            mongo.close();
		}
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Boolean authentication) {
		this.authentication = authentication;
	}
}
