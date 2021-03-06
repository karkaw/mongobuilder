package org.damuzee.mongo;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 连接池的工作原理主要由三部分组成，分别为连接池的建立，连接池中连接的使用管理，连接池的关闭。
 *第一、连接池的建立。一般在系统初始化时，连接池会根据系统配置建立，并在池中建立几个连接对象，
 *      以便使用时能从连接池中获取，连接池中的连接不能随意创建和关闭，这样避免了连接随意建立和关闭造成的系统开销。
 *      java中提供了很多容器类，可以方便的构建连接池，例如Vector,stack等。
 *第二、连接池的管理。连接池管理策略是连接池机制的核心，连接池内连接的分配和释放对系统的性能有很大的影响。其策略是：
 *      当客户请求数据库连接时，首先查看连接池中是否有空闲连接，如果存在空闲连接，则将连接分配给客户使用；如果没有控线连接，则查看当前所开的连接数是否已经达到最大连接数，例如如果没有达到就重新创建一个请求的客户；如果达到，就按设定的最大等待时间进行等待，如果超出最大等待时间，则抛出异常给客户。
 *      当客户释放数据库连接时，先判断该连接的引用次数是否超过了规定值，如果超过了就从连接池中删除该连接，否则就保留为其他客户服务。该策略保证了数据库连接的有效复用，避免了频繁建立释放连接所带来的系统资源的开销。
 *第三、连接池的关闭。当应用程序退出时，关闭连接池中所有的链接，释放连接池相关资源，该过程正好与创建相反。
 * Created by karka.w on 2015/10/28.
 */

public class MongoPool {

    public final static int DEFAULT_POOL_SIZE   = 4;

    private final ConcurrentMap connects = new ConcurrentHashMap();

}
