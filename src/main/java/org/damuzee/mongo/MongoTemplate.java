package org.damuzee.mongo;

import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;


/**
 * Mongodb 通用操作类
 */
public class MongoTemplate {

    private static  final Logger logger = LoggerFactory.getLogger(MongoTemplate.class);

    private MongoFactory mongoFactory;

    public MongoTemplate(MongoFactory mongoFactory) {
        this.mongoFactory = mongoFactory;
        logger.info("--------------------------: {}", mongoFactory);
    }

    public MongoCollection getCollection(String collection) {

        MongoDatabase database =  mongoFactory.getDatabase(); //driver已经缓存
        MongoCollection collect  = database.getCollection(collection);
        logger.debug(" db {} , getCollection : {}", database, collection);
        return collect;
    }

    /**
     * 按条件 删除
     * @param tableName
     * @param query
     */
    public void findAndRemove(String tableName ,Map query){
        logger.debug("findAndRemove collection {} ,params {}",tableName,query);

        MongoCollection collection = getCollection(tableName);

        collection.findOneAndDelete(new SimpleMapObject());
    }

    /**
     * 统计
     *
     * @param tableName
     * @param params
     * @return
     */
    public List<Document> aggregate(String tableName ,Map ... params){
        logger.debug("aggregate tableName {} ,params {}",tableName,params);

        MongoCollection collection = getCollection(tableName);

        final AggregateIterable iterable  = collection.aggregate(Arrays.asList(params));

        final List<Document> result = new ArrayList();
        iterable.forEach(new Block<Document>() {
            public void apply(final Document document) {
                Object id = document.get(MongoConstaints.ID);
                if (!(id instanceof String)){
                    document.put(MongoConstaints.ID, String.valueOf(id));
                }
                result.add(document);
            }
        });

        logger.debug("result : {}", result);
        return  result;
    }

    /**
     * 若存在主键， save() 则更改原来的文档
     *
     * @param object
     * @return
     */
    public String save(String tableName, Map object) {
        logger.debug("save tableName {} ,params {}", tableName, object);
        MongoCollection collection = getCollection(tableName);
        Document document = new Document(object);
        collection.insertOne(document);

        return new SimpleMapObject(document).getId();
    }

    /**
     * 更新文档
     *
     * @param update
     */
    public void update(String tableName, Map update) {
        update(tableName, null, update, true, true);
    }

    /**
     * 更新文档
     *
     * @param update
     */
    public void update(String tableName,Map query,Map update) {
        update(tableName, query, update, false, true);
    }

    /**
     * 更新文档
     *
     * @param tableName 文档名称
     * @param update		更新内容
     * @param query		查询条件
     */
    public void update(String tableName,Map query,Map update,Boolean upsert , Boolean multi ) {

        logger.debug("update tableName {} ,query {}  ,update {}  ,upsert {}  ,multi {}  ", tableName, query, update, upsert, multi);

        MongoCollection collection = getCollection(tableName);
        if(update == null ){
            throw new NullPointerException("update null exception");
        }

        if(query == null){
            query = new SimpleMapObject(MongoConstaints.ID,new ObjectId((String)update.remove(MongoConstaints.ID)));
        }

        Set<Map.Entry<String, Object>> entries = update.entrySet();
        boolean startWith$ = true;
        for(Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            if(!key.startsWith("$")){
                startWith$ = false;
                logger.warn("更新数据的时候，建议在update的map key值中使用更新符号，比如$set, $pull, $push, $addToSet……");
                break;
            }
        }

        if(!startWith$){
            update = new SimpleMapObject().set(update);
        }

        UpdateOptions updateOptions = new UpdateOptions();
        updateOptions.upsert(upsert);
        if(multi){
            collection.updateMany(new SimpleMapObject(query), new SimpleMapObject(update),updateOptions);
        }else {
            collection.updateOne(new SimpleMapObject(query), new SimpleMapObject(update),updateOptions);
        }
    }

    /**
     * 更新文档
     *
     * @param query
     */
    @Deprecated
    public void arrayUpdate(String tableName, Map query, Map update) {

        logger.debug("arrayUpdate tableName {} ,query {}  ,update {}",tableName,query,update);

        MongoCollection collection = getCollection(tableName);

        collection.updateMany(new SimpleMapObject(query), new SimpleMapObject(update));
    }

    /**
     * 删除表中某一个数组中的一条数据。
     * @param tableName
     * @param query
     * @param delete
     * @author Chyohn Shaw
     */
    public void deleteFromArray(String tableName, Map query, Map delete){
        logger.debug("deleteFromArray tableName {} ,query {}  ,delete {} ",tableName,query,delete);

        MongoCollection collection = getCollection(tableName);
        Bson update = new BasicDBObject("$pull", delete);
        collection.updateOne(new SimpleMapObject(query), update);
    }

    /**
     * 通过Id删除文档
     *
     * @param query
     */
    public void deleteByIds(String tableName,Map query) {
        logger.debug("deleteByIds tableName {} ,query {} ",tableName,query);

        MongoCollection collection = getCollection(tableName);
        if(query.get(MongoConstaints.ID) != null) {
            deleteById(tableName,query.get(MongoConstaints.ID).toString());
        }else{
            collection.deleteOne(new SimpleMapObject(query));
        }
    }

    /**
     * 通过查询删除文档
     *
     * @param id
     * @return 删除的总数
     */
    public long deleteById(String tableName, String id) {

        logger.debug("deleteById tableName {} ,id {}   ",tableName,id);

        MongoCollection collection = getCollection(tableName);

        Bson obj = new BasicDBObject(MongoConstaints.ID, new ObjectId(id));

        logger.debug("result : {}",obj);

        DeleteResult result = collection.deleteOne(obj);
        long count = result.getDeletedCount();

        return count;
    }


    /**
     * 查找方法
     *
     * @param where
     * @return
     */
    public List< Map<String, Object>>  find(String tableName, Map  where) {

        return  find(tableName,where,null,0,0);
    }

    public long count(String tableName, Map where){
        MongoCollection collection = getCollection(tableName);
        long count = 0;
        if(where !=null){
            count = collection.count(new SimpleMapObject(where));
        }  else  {
            count = collection.count();
        }

        return count;
    }

    /**
     * 查找方法
     *
     * @param where
     * @param filter
     * @return
     */
    public  List find(String tableName,Map  where,Map filter) {

        return  find(tableName,where,filter,0,0);
    }


    public  List find(String tableName, Map  where,Map filter,Integer skip,Integer limit) {

        return find(tableName,where,filter,null,skip,limit);
    }

    public List find(String tableName, Map  where,Map filter,Map sort) {
        return find(tableName, where, filter, sort, 0, 0);
    }

    public  List find(String tableName, Map  where,Map filter,Map sortBy,Integer skip,Integer limit) {
        logger.debug("find tableName {} ,where {}  ,filter {} ,sortBy {},skip {} ,limit{}",tableName,where,filter,sortBy,skip,limit);

        MongoCollection collection = getCollection(tableName);

        FindIterable cursor  = null ;
        if(where != null){
            cursor = collection.find(new SimpleMapObject(where));
        }else{
            cursor = collection.find();
        }
        if(filter != null){
            cursor.projection(new SimpleMapObject(filter));
        }

        if(sortBy!= null){
            cursor.sort(new SimpleMapObject(sortBy));
        }
        if(skip != null && skip != 0){
            cursor.skip(skip);
        }
        if(limit != null && limit != 0){
            cursor.limit(limit);
        }

        final List<Document> resultList = new ArrayList< Document>();

        cursor.forEach(new Block<Document>() {
            public void apply(final Document document) {
                Object id = document.get(MongoConstaints.ID);
                if (!(id instanceof String)){
                    document.put(MongoConstaints.ID, String.valueOf(id));
                }
                resultList.add(document);
            }
        });
        logger.debug("result : {}",resultList);

        return resultList;
    }

    /**
     * 查找一条记录
     *
     * @param where
     * @return
     */
    public Map<String,Object> findOne(String tableName, Map  where) {

        Map result = findOne(tableName,where,null);

        logger.debug("result : {}",result);

        return result;

    }

    public int getSequence(String tableName){
        logger.debug("getSequence tableName {}", tableName);

        MongoCollection coll = getCollection("sequence");

        SimpleMapObject query = new SimpleMapObject();
        query.put("_id", tableName);

        FindIterable seq = coll.find(query);
        if(seq == null){
            coll.insertOne(query);
        }

        SimpleMapObject newDocument =new SimpleMapObject();
        newDocument.put("$inc", new SimpleMapObject("seq", 1));

        FindOneAndUpdateOptions updateOptions = new FindOneAndUpdateOptions();
        updateOptions.upsert(true);
        Document ret = (Document)coll.findOneAndUpdate(query, newDocument, updateOptions);

        if (ret == null){
            return 0;
        }
        Integer result = (Integer)ret.get("seq") + 1 ;

        logger.debug("getSequence {} ,result : {}",tableName,result);

        return result;
    }


    /**
     * 查找一条记录
     *
     * @param where
     * @param filter
     * @return
     */
    public Map<String,Object> findOne(String tableName, Map  where,Map filter) {

        logger.debug("findOne tableName {} ,where {}  ,filter {}",tableName,where,filter);

        MongoCollection collection = getCollection(tableName);

        FindIterable iterable = collection.find(new SimpleMapObject(where));
        if(filter != null){
            iterable.projection(new SimpleMapObject(filter));
        }

        Document result = (Document)iterable.first();
        if(result== null){
            return result;
        }

        Object id = result.get(MongoConstaints.ID);
        if (!(id instanceof String)){
            result.put(MongoConstaints.ID, String.valueOf(id));
        }

        logger.debug("result : {}", iterable);

        return result ;
    }



    /**
     * 更新文档
     *
     * @param tableName 文档名称
     * @param update		更新内容
     * @param query		查询条件
     */
    public void findAndModify(String tableName,Map query,Map update) {
        logger.debug("findAndModify tableName {} ,query {}  ,update {} ",tableName,query,update);

        MongoCollection collection = getCollection(tableName);
        if(update == null ){
            throw new NullPointerException("update null exception");
        }

        Set<Map.Entry<String, Object>> entries = update.entrySet();
        boolean startWith$ = true;
        for(Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            if(!key.startsWith("$")){
                startWith$ = false;
                logger.warn("更新数据的时候，建议在update的map key值中使用更新符号，比如$set, $pull, $push, $addToSet……");
                break;
            }
        }

        if(!startWith$){
            update = new SimpleMapObject().set(update);
        }
        collection.findOneAndUpdate(new SimpleMapObject(query), new SimpleMapObject(update));
    }
    /**
     * 分页查询
     *
     * @param tableName
     * @param query
     * @param filter
     * @param sort
     * @param current
     * @param limit
     * @return
     */
    public Map<String, Object> findByPage(String tableName, Map query, Map filter,Map sort,Integer current,Integer limit) {
        logger.debug("findByPage tableName {} ,where {}  ,filter {} ,current {} ,limit{}",tableName,query,filter,current,limit);

        //SimpleMapObject sort  =;.

        if(current == null){
            current = 1;
        }
        if(limit == null){
            limit = 10 ;
        }

        MongoCollection collection = getCollection(tableName);

        FindIterable iterable;
        if (query  == null) {
            if (filter == null) {
                iterable = collection.find();
            } else {
                iterable = collection.find(new BasicDBObject()).projection( new SimpleMapObject(filter));
            }
        } else {
            SimpleMapObject where =  new SimpleMapObject(query);
            //如果数据库有mongodb的自身_id
            Object _id = where.get(MongoConstaints.ID);
            if (_id != null) {
                where.put(MongoConstaints.ID, new ObjectId(String.valueOf(_id)));
            }
            if (filter == null) {
                iterable = collection.find(where);
            } else {
                iterable = collection.find(where).projection( new SimpleMapObject(filter));
            }
        }

        //统计总数
        Long count = collection.count();

        if (sort != null) {
            iterable.sort( new SimpleMapObject(sort));
        }
        if(current != null && limit != null){
            int skip = current == 0  ? current :(current-1) * limit  ;
            if (skip > 0) {
                iterable.skip(skip);
            }
        }

        if (limit != null && limit > 0) {
            iterable.limit(limit);
        }

        final List<Object>  resultList = new ArrayList();
        iterable.forEach(new Block() {
            @Override
            public void apply(Object o) {
                Document document = (Document)o;
                Object id = document.get(MongoConstaints.ID);
                if (!(id instanceof String)){
                    document.put(MongoConstaints.ID, String.valueOf(id));
                }
                resultList.add(o);
            }
        });

        SimpleMapObject paramsMap = new SimpleMapObject();
        Long size = Long.valueOf(1);
        if(count != null && limit != null ) {
            size = limit == 0 ? 1 : (count + limit - 1) / limit;
        }
        paramsMap.append(MongoConstaints.PAGE_TOTAL, size);
        paramsMap.append(MongoConstaints.PAGE_CURRENT, current);
        paramsMap.append(MongoConstaints.PAGE_COUNT, count)
                .append(MongoConstaints.PAGE_RESULT, resultList);

        logger.debug("result : {}",paramsMap);

        return paramsMap;
    }

}