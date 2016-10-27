package org.damuzee.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;

import javax.script.SimpleBindings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 适合mongodb 文档的数据结构
 */
public class SimpleMapObject<T extends SimpleMapObject> extends BasicDBObject  implements DBObject,Map<String, Object>{

    public static final String ID = "_id";

    public SimpleMapObject() { }

    public SimpleMapObject(String id) {
        this.setObjectId(id);
    }

    public SimpleMapObject(String key, Object object) {
        this.put(key, object);
    }

    public SimpleMapObject(Map<String, Object> warp) {
        if (warp != null) {
            this.putAll(warp);
        }
    }

    public String getId() {
        Object id = this.get(MongoConstaints.ID);
        return String.valueOf(id);
    }

    public T setId(String id) {
        this.put(MongoConstaints.ID, id);
        return (T)this;
    }

    public T setObjectId(String id) {
        if (StringUtils.isNotBlank(id)) {
            this.put(MongoConstaints.ID, new ObjectId(id));
        }
        return (T)this;
    }

    public Map getMap(String key){
        Object map = this.get(key);
        if (map == null) {
            return null;
        }
        return (Map)map;
    }

    public Collection getCollection(String key) {
        Object collection = this.get(key);
        if (collection == null) {
            return null;
        }
        return (Collection) collection;
    }

    public List getList(String key) {
        return (List)getCollection(key);
    }

    /**
     * 把字符串ID转换为{@link ObjectId}
     * @param ids
     * @return
     */
    public static List<ObjectId> convertToObjectIds(List<String> ids) {
        List<ObjectId> objectIds = new ArrayList<>();
        if (ids == null) return objectIds;
        for (String id : ids) {
            objectIds.add(new ObjectId(id));
        }
        return objectIds;
    }

    /**
     * 根据Map中指定key对应的值放入List对象中
     * @param datas
     * @param key
     * @return
     */
    public static List elementsAsList(List<Map<String, Object>> datas, String key){
        List result = new ArrayList();
        for(Map<String, Object> element : datas) {
            Object o = element.get(key);
            if (o != null) {
                result.add(o);
            }
        }
        return result;
    }

    /**
     * 把源数据的值根据Key拷贝到新的map中
     *
     * @param source 源
     * @param keys
     * @return
     */
    public SimpleMapObject copy(Map source, String... keys) {
        for (String key : keys) {
            this.append(key, source.get(key));
        }
        return this;
    }

    /**
     * 构建Mongodb是否显示字段的Map
     *
     * @param visible true 表示显示Keys中的字段 false 除开keys中的字段其它的都显示
     * @param keys
     * @return
     */
    public static SimpleMapObject filter(boolean visible, String... keys) {
        SimpleMapObject SimpleMapObject = new SimpleMapObject();
        for (String key : keys) {
            if (visible) {
                SimpleMapObject.put(key, 1);
            } else {
                SimpleMapObject.put(key, 0);
            }
        }
        return SimpleMapObject;
    }


    public static SimpleMapObject match(Map match) {
        SimpleMapObject SimpleMapObject = new SimpleMapObject();
        SimpleMapObject.put(MongoConstaints.MATCH, match);
        return SimpleMapObject;
    }

    public static SimpleMapObject project(Map project) {
        SimpleMapObject SimpleMapObject = new SimpleMapObject();
        SimpleMapObject.put(MongoConstaints.PROJECT, project);
        return SimpleMapObject;
    }

    public static SimpleMapObject group(Map group) {
        SimpleMapObject SimpleMapObject = new SimpleMapObject();
        SimpleMapObject.put(MongoConstaints.GROUP, group);
        return SimpleMapObject;
    }

    public static SimpleMapObject unwind(String keys) {
        SimpleMapObject SimpleMapObject = new SimpleMapObject();
        SimpleMapObject.put(MongoConstaints.UNWIND, keys);
        return SimpleMapObject;
    }

    public static SimpleMapObject or(Object project) {
        SimpleMapObject SimpleMapObject = new SimpleMapObject();
        SimpleMapObject.put(MongoConstaints.OR, project);
        return SimpleMapObject;
    }

    public static SimpleMapObject nin(Object nin) {
        SimpleMapObject SimpleMapObject = new SimpleMapObject();
        SimpleMapObject.put(MongoConstaints.NIN, nin);
        return SimpleMapObject;
    }

    /**
     * 倒序
     */
    public   SimpleMapObject desc(String ... keys) {
        for (String key :keys){
            this.put(key, -1);
        }
        return this;
    }


    /**
     * 升序
     */
    public   SimpleMapObject asc(String ... keys) {
        for (String key :keys) {
            this.put(key, 1);
        }
        return this;
    }

    /**
     *
     * @param values
     * @return
     * @author Chyohn Shaw
     */
    public SimpleMapObject nin(String key, Object... values) {
        List list = new ArrayList();
        for (Object o : values) {
            list.add(o);
        }
        SimpleMapObject notequal = new SimpleMapObject(MongoConstaints.NIN, list);
        this.put(key, notequal);
        return this;
    }

    /**
     *
     * @param values
     * @return
     * @author Chyohn Shaw
     */
    public SimpleMapObject in(String key, Object... values) {
        List list = new ArrayList();
        for (Object o : values) {
            list.add(o);
        }
        SimpleMapObject notequal = new SimpleMapObject(MongoConstaints.IN, list);
        this.put(key, notequal);
        return this;
    }

    /**
     * @param value
     * @return
     * @author kk
     */
    public SimpleMapObject in(String key, Object value) {
        List in = null ;
        if(value instanceof List){
            in = (List)value;
        }else{
            Map  map  = (Map)this.get(key);
            if(map != null){
                in = (List)map.get(MongoConstaints.IN);
                if(in == null){
                    in = new ArrayList();
                    in.add(value);
                }
                in.add(value);
            } else {
                in = new ArrayList();
                in.add(value);
            }
        }
        SimpleMapObject notequal = new SimpleMapObject(MongoConstaints.IN, in);
        this.put(key, notequal);
        return this;
    }

    /**
     * 修改数据
     *
     * @param set
     * @return
     */
    public SimpleMapObject set(Object set) {
        this.put(MongoConstaints.SET, set);
        return this;
    }



    /**
     * 数组中新增
     *
     * @param key
     * @param value
     * @return
     */
    public  SimpleMapObject  push(String key, Object value) {
        SimpleMapObject push = (SimpleMapObject)this.get(MongoConstaints.PUSH);
        if(push != null){
            push.put(key, value);
        } else {
            this.put(MongoConstaints.PUSH, new SimpleMapObject(key, value));
        }
        return this;
    }

    /**
     * 如果数组中没有该数据，向数组中添加数据，如果该数组中有相同数组，不添加
     * @param key
     * @param data
     * @return
     */
    public SimpleMapObject addToSet(String key, Object data) {
        this.put(MongoConstaints.ADDTOSET, new SimpleMapObject(key, data));
        return this;
    }

    public  T inc(String key,Object value){
        Map inc = new SimpleBindings();
        if (this.get(MongoConstaints.INC) != null) {
            inc = (Map)this.get(MongoConstaints.INC);
        }
        inc.put(key, value);
        this.put(MongoConstaints.INC, inc);
        return (T)this;
    }

    public  T inc(SimpleMapObject incM ){
        Map inc = new SimpleBindings();
        if (this.get(MongoConstaints.INC) != null) {
            inc = (Map)this.get(MongoConstaints.INC);
        }
        inc.putAll(incM);
        this.put(MongoConstaints.INC, inc);
        return (T)this;
    }

    /**
     * $each运算符操作
     * @param array
     * @return
     */
    public static SimpleMapObject each(Object[] array){
        SimpleMapObject map = new SimpleMapObject();
        map.put(MongoConstaints.EACH, array);
        return map;
    }

    /**
     * $each运算符操作
     * @param list
     * @return
     */
    public static SimpleMapObject each(List list){
        SimpleMapObject map = new SimpleMapObject();
        map.put(MongoConstaints.EACH, list);
        return map;
    }

    public SimpleMapObject  pull(String key, Object pull){
        this.put(MongoConstaints.PULL, new SimpleMapObject(key, pull));
        return this;
    }

    public SimpleMapObject  pop(String key, int order){
        this.put(MongoConstaints.POP, new SimpleMapObject(key, order));
        return this;
    }

    /**
     * 或
     *
     * @param ors
     * @return
     */
    public SimpleMapObject or(Map... ors) {
        List list = new ArrayList();
        for (Map m : ors) {
            list.add(m);
        }
        this.put(MongoConstaints.OR, list);
        return this;
    }

    /**
     *
     * @param value
     * @return
     * @author Chyohn Shaw
     */
    public SimpleMapObject not(String key, Object value) {
        SimpleMapObject notequal = new SimpleMapObject(MongoConstaints.NOT, value);
        this.put(key, notequal);
        return this;
    }


    public static SimpleMapObject limit(Map limit) {
        SimpleMapObject SimpleMapObject = new SimpleMapObject();
        SimpleMapObject.put(MongoConstaints.LIMIT, limit);
        return SimpleMapObject;
    }

    public static SimpleMapObject skip(Map skip) {
        SimpleMapObject SimpleMapObject = new SimpleMapObject();
        SimpleMapObject.put(MongoConstaints.SKIP, skip);
        return SimpleMapObject;
    }

    /**
     * 附近
     *
     * @param location
     * @param radius
     * @return
     */
    public static SimpleMapObject getCenter(double [] location , int radius){

        SimpleMapObject centerSphere = new SimpleMapObject();
        Object [] local = {location,radius};
        centerSphere.put("$centerSphere",local);

        SimpleMapObject geoWithin = new SimpleMapObject();
        geoWithin.put("$geoWithin",centerSphere);

        return geoWithin;
    }

    /**
     *
     * @param location 当前坐标
     *
     * @param radius 指定最大距离
     * @return
     */
    public static SimpleMapObject near(double [] location,Object radius){
        SimpleMapObject near = new SimpleMapObject();
        near.put("$near",location);
        if(radius != null){
            near.put("$maxDistance",radius);
        }
        return near;
    }

    /**
     * 平均数
     *
     * @param avg
     * @return
     */
    public static SimpleMapObject avg(String avg) {
        SimpleMapObject SimpleMapObject = new SimpleMapObject();
        SimpleMapObject.put(MongoConstaints.AVG, "$" + avg);
        return SimpleMapObject;
    }

    /**
     * 不等于
     *
     * @param key
     * @param value
     * @return
     */
    public  SimpleMapObject ne(String key ,Object value){
        this.put(key, new SimpleMapObject("$ne",value));
        return  this;
    }

    /**
     * 数组in
     *
     * @param in
     * @return
     */
    public static  SimpleMapObject in(Object [] in){
        SimpleMapObject SimpleMapObject = new SimpleMapObject();
        SimpleMapObject.put(MongoConstaints.IN, in);
        return  SimpleMapObject;
    }

    /**
     * 数组in
     *
     * @param in
     * @return
     */
    public static  SimpleMapObject in(Object in){
        SimpleMapObject SimpleMapObject = new SimpleMapObject();
        SimpleMapObject.put(MongoConstaints.IN, in);
        return  SimpleMapObject;
    }

    /**
     * 总和
     * @param key
     * @return
     */
    public static SimpleMapObject sum(Object key){
        SimpleMapObject SimpleMapObject = new SimpleMapObject() ;
        if (key== null){
            SimpleMapObject.put(MongoConstaints.SUM,1 ) ;
        }else if (key instanceof String){
            SimpleMapObject.put(MongoConstaints.SUM,"$"+ key) ;
        }else {
            SimpleMapObject.put(MongoConstaints.SUM,key) ;
        }
        return SimpleMapObject ;
    }

    /**
     * 除
     * @param key1
     * @param key2
     * @return
     */
    public static SimpleMapObject divide(String key1,String key2){
        SimpleMapObject SimpleMapObject = new SimpleMapObject() ;
        String [] divide = {"$" +key1 ,"$" + key2 };
        SimpleMapObject.put(MongoConstaints.DIVIDE,divide);
        return SimpleMapObject;
    }


    /**
     * 完全匹配
     *
     * @param key
     * @param value
     * @return
     */

    public SimpleMapObject allLike(String key,String value){
        Pattern pattern = Pattern.compile("^"+value+"$", Pattern.CASE_INSENSITIVE);
        this.put(key,pattern);
        return this;
    }

    /**
     * 右匹配
     *
     * @param key
     * @param value
     * @return
     */
    public SimpleMapObject rightLike(String key,String value){
        Pattern pattern = Pattern.compile("^.*"+value+"$", Pattern.CASE_INSENSITIVE);
        this.put(key,pattern);
        return this;
    }

    /**
     * 左匹配
     *
     * @param key
     * @param value
     * @return
     */
    public SimpleMapObject leftLike(String key,String value){
        Pattern pattern = Pattern.compile("^"+value+".*$", Pattern.CASE_INSENSITIVE);
        this.put(key,pattern);
        return this;
    }

    /**
     * 模糊匹配
     *
     * @param key
     * @param value
     * @return
     */
    public SimpleMapObject fulllike(String key,String value){
        Pattern pattern = Pattern.compile("^.*"+value+".*$", Pattern.CASE_INSENSITIVE);
        this.put(key,pattern);
        return this;
    }

}