package org.damuzee.mongo;

/**
 * Created by karka.w on 2014/9/19.
 */
public class MongoConstaints {

    public static final String ID = "_id" ;

    public static final String OR = "$or";

    public static final String AND = "$and";

    public static final String PROJECT = "$project";

    public static final String MATCH = "$match";

    public static final String UNWIND = "$unwind";

    public static final String GROUP = "$group";

    public static final String NIN = "$nin";

    /**
     * 修改器
     *
     */
    public static final String INC = "$inc";
    public static final String SET = "$set";
    public static final String PUSH = "$push";
    public static final String ADDTOSET = "$addToSet";
    public static final String PULL = "$pull";
    public static final String POP = "$pop";

    public static final String EACH = "$each";

    public static final String LIMIT = "$limit" ;

    public static final String SKIP = "$skip" ;

    public static final String AVG = "$avg" ;

    /** added by Chyohn Shaw **/
    public static final String NOT = "$not";
    /** added by Chyohn Shaw **/
    public static final String IN = "$in";

    public static final String SUM = "$sum";

    public static final String DIVIDE  = "$divide" ;
    public static final String LTE  = "$lte" ;
    public static final String GTE  = "$gte" ;

    /**分页的参数**/
    public static final String PAGE_PARAMS = "params" ;
    /**显示字段的参数**/
    public static final String PAGE_FILTER = "filter" ;
    /**排序的参数**/
    public static final String PAGE_SORT= "sort" ;

    /**总条数**/
    public static final String PAGE_COUNT = "totalCount" ;
    /**查询结果**/
    public static final String PAGE_RESULT= "result" ;
    /**每页显示条数**/
    public static final String PAGE_LIMIT = "pageSize" ;
    /**当前页**/
    public static final String PAGE_CURRENT= "pageNum" ;
    /**总页数**/
    public static final String PAGE_TOTAL= "totalPage" ;

    /**默认当前页码**/
    public static final int DEFAULT_PAGE_CURRENT=1;
    /**默认当前每页条数**/
    public static final int DEFAULT_PAGE_LIMIT_NUM=10;
    /**默认总页数**/
    public static final int DEFAULT_PAGE_ALL_RECORD=0;

    /**半径radius**/
    public static final int RADIUS=10;

    /**搜索人数**/
    public static final int MERMBER_COUNT=10;

}