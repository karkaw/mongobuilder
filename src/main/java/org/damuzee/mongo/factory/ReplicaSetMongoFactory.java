package org.damuzee.mongo.factory;

import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import org.damuzee.mongo.MongoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 支持高性能ReplicaSet集群的 MongodbFactory
 */
public class ReplicaSetMongoFactory implements MongoFactory {

    private static final Logger logger = LoggerFactory.getLogger(ReplicaSetMongoFactory.class);

    private String host;//主机地址
    private int port;//端口地址
    private String username; //用户名
    private String password; //密码
    private String dbName;//数据库名
    private int connectTimeout = 5000;
    private int socketTimeout = 5000;
    private int maxWaitTime = 1000 * 60 * 2;
    private int connectionsPerHost = 20;//每台主机最大连接数
    private int connectionThreads = 10;//线程队列数
    private Boolean authentication = false;//是否需要身份验证

    private static final int DEFAULT_PORT = 27017;

    private Mongo mongo = null;
    private  MongoDatabase db = null ;

    private MongoClientOptions getConfOptions() {
        //连接池参数设置
        MongoClientOptions clientOptions = new MongoClientOptions.Builder()
                .socketKeepAlive(true) // 是否保持长链接
                .connectTimeout(connectTimeout) // 链接超时时间
                .socketTimeout(socketTimeout) // read数据超时时间
                .readPreference(ReadPreference.primary()) // 最近优先策略
                .connectionsPerHost(connectionsPerHost) // 每个地址最大请求数
                .maxWaitTime(maxWaitTime) // 长链接的最大等待时间
                .threadsAllowedToBlockForConnectionMultiplier(connectionThreads) // 一个socket最大的等待请求数
                .writeConcern(WriteConcern.UNACKNOWLEDGED)
                .build();
        return clientOptions;
    }

    public MongoDatabase getDatabase(){
        logger.info("Mongodb db info ,db {} , host {} , port {} ", dbName, host, port);
        if(db == null){
            //集群列表
            List<ServerAddress> replicaSetSeeds = new ArrayList();
            String[] hosts = host.split(",");
            for (int i = 0; i < hosts.length; i++) {
                String[] url = hosts[i].split(":");
                String realHost = null;
                int realPort = port == 0 ? DEFAULT_PORT : port;
                if (url.length > 0) {
                    realHost = url[0];
                }
                if (url.length > 1) {
                    realPort = Integer.parseInt(url[1]);
                }
                replicaSetSeeds.add(new ServerAddress(realHost, realPort));
            }

            MongoClient client = null;
            if(authentication) {
                logger.info("Mongodb auth username {} , password {}, db {}",username,password,dbName);
                MongoCredential credentials = MongoCredential.createScramSha1Credential(username, dbName, password.toCharArray());
                List<MongoCredential> credentialsList = new ArrayList<MongoCredential>();
                credentialsList.add(credentials);
                client = new MongoClient(replicaSetSeeds, credentialsList,getConfOptions());
            }else {
                client = new MongoClient(replicaSetSeeds);
            }
            db = client.getDatabase(dbName);
        }
        return  db;
    }

    @Override
    public void destroy() throws Exception {
        logger.info("Mongodb db closeing ...........");
        if (mongo != null) {
            mongo.close();
            mongo = null;
        }
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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

    public int getConnectionsPerHost() {
        return connectionsPerHost;
    }

    public void setConnectionsPerHost(int connectionsPerHost) {
        this.connectionsPerHost = connectionsPerHost;
    }

    public int getConnectionThreads() {
        return connectionThreads;
    }

    public void setConnectionThreads(int connectionThreads) {
        this.connectionThreads = connectionThreads;
    }

    public Boolean getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Boolean authentication) {
        this.authentication = authentication;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(int maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }
}
