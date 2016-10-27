## Mongodb的工具

** 基于Mongo3.x的封装

-------------------
功能：
* 1）对Mongodb的简单操作

-------------------
### 开发环境
* IDE：idea 15
* JDK：jdk1.7
* WINDOWS：mvn 3

-------------------
Spring初始化
*    <bean id="mongoFactory" class="org.damuzee.mongo.factory.ReplicaSetMongoFactory">
*        <property name="host" value="${mongo.host}"/>
*        <property name="port" value="${mongo.port}"/>
*        <property name="username" value="${mongo.username}"/>
*        <property name="password" value="${mongo.password}"/>
*        <property name="authentication" value="${mongo.auth}"/>
*        <property name="connectTimeout" value="${mongo.connectTimeout}"/>
*        <property name="socketTimeout" value="${mongo.socketTimeout}"/>
*        <property name="maxWaitTime" value="${mongo.maxWaitTime}"/>
*        <property name="connectionsPerHost" value="${mongo.connectionsPerHost}"/>
*        <property name="connectionThreads" value="${mongo.connectionThreads}"/>
*        <property name="dbName" value="${mongo.dbname}"></property>
*    </bean>

*    <bean id="mongoTemplate"  class="org.damuzee.mongo.MongoTemplate">
*        <constructor-arg ref="mongoFactory" />
*    </bean>
-------------------