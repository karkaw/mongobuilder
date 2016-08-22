package org.damuzee.test.mongodb;

import org.damuzee.mongo.MongoTemplate;
import org.damuzee.mongo.SimpleMapObject;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.util.List;
import java.util.Map;

/**
 * Created by karka.w on 2015/7/17.
 */
@ContextConfiguration(locations = {"classpath:application-mongo.xml"})
public class TaskMongoMap extends AbstractJUnit4SpringContextTests {

    @Autowired
    MongoTemplate template;

    @Test
    public void save() {
        SimpleMapObject save = new SimpleMapObject();
        save.append("username", "admin");
        save.append("password", "admin");
        save.append("age", 12);
        save.append("nickname", "kk");
        template.save("test_name", save);
    }

    @Test
    public void find() {
        SimpleMapObject save = new SimpleMapObject();
        save.put("username", "admin");
        save.put("password", "admin");
        List list = template.find("test_name", save);

    }

    @Test
    public void findPage() {
        //Document{{location=Document{{$near=[D@f42f50c, $maxDistance=10}}, state=1020, creater=Document{{$ne=18708155583}}, type=Document{{$in=[null]}}}}
        SimpleMapObject query = new SimpleMapObject();
        query.put("username", "admin");
        query.put("password", "admin");
        double [] dd = {10.1,10.1};
        query.put("location",SimpleMapObject.near(dd,1));
        Map page = template.findByPage("task",query,null,null,1,10);
        System.out.println("page :  " + page.get( 1));
    }

    @Test
    public void findOne() {
        SimpleMapObject save = new SimpleMapObject();
        save.put("username", "admin");
        save.put("password", "admin");

        SimpleMapObject filter = SimpleMapObject.filter(true, "username");
        Map one = template.findOne("test_name", save, filter);
        System.out.println(one);
    }

    @Test
    public void findSqeunce() {

        SimpleMapObject save = new SimpleMapObject();
        save.append("username", "admin");
        save.append("password", "admin");

        SimpleMapObject filter = SimpleMapObject.filter(true, "username");
        Integer result = template.getSequence("test_name");
        System.out.println(result);
    }


}
