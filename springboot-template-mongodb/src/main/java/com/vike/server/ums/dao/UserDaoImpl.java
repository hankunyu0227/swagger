package com.vike.server.ums.dao;

import com.alibaba.fastjson.JSONObject;
import com.vike.server.ums.model.UserDetails;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class UserDaoImpl implements UserDao {

    @Autowired
    private MongoTemplate template;

    @Override
    public List<UserDetails> getUser(Boolean countOutput, Integer start, Integer limit, String key, String value,
                                     Boolean search, Boolean searchByAny, String sortfield, String sorttype, String fields) {
        Document fieldsDoc = new Document();
        if (fields != null) {
            String[] fieldArray = fields.split(",");
            for (String field : fieldArray) {
                fieldsDoc.put(field, true);
            }
        }
        Query query = new BasicQuery(new Document(), fieldsDoc);
        // 查询条件处理
        if (searchByAny == null || !searchByAny) {
            if (search == null || !search) {
                if (key != null && value != null) {
                    String[] keys = key.split(",");
                    String[] values = value.split(",");
                    for (int i = 0; i < values.length; i++) {
                        Criteria criteria = Criteria.where(keys[i]).is(values[i]);
                        query.addCriteria(criteria);
                    }
                }
            } else {
                if (key != null && value != null) {
                    String[] keys = key.split(",");
                    String[] values = value.split(",");
                    for (int i = 0; i < values.length; i++) {
                        Criteria criteria = Criteria.where(keys[i]).regex(values[i]);
                        query.addCriteria(criteria);
                    }
                }
            }
        } else {
            if (search == null || !search) {
                if (key != null && value != null) {
                    String[] keys = key.split(",");
                    String[] values = value.split(",");
                    Criteria[] condition = new Criteria[keys.length];
                    for (int i = 0; i < values.length; i++) {
                        condition[i] = Criteria.where(keys[i]).is(values[i]);
                    }
                    Criteria criteria = new Criteria();
                    criteria.orOperator(condition);
                    query.addCriteria(criteria);
                }
            } else {
                if (key != null && value != null) {
                    String[] keys = key.split(",");
                    String[] values = value.split(",");
                    Criteria[] condition = new Criteria[keys.length];
                    for (int i = 0; i < values.length; i++) {
                        condition[i] = Criteria.where(keys[i]).regex(values[i]);
                    }
                    Criteria criteria = new Criteria();
                    criteria.orOperator(condition);
                    query.addCriteria(criteria);
                }
            }
        }
        if (sortfield != null && sorttype != null) {
            switch (sorttype) {
                case "ASC":
                    query.with(Sort.by(Sort.Order.asc(sortfield)));
                    break;
                case "DESC":
                    query.with(Sort.by(Sort.Order.desc(sortfield)));
                    break;
                default:
                    break;
            }
        }
        template.count(query, UserDetails.class);
        if (start != null && limit != null) {
            query.skip(start).limit(limit);
        }
        List<UserDetails> resultList = template.find(query, UserDetails.class);
        return resultList;
    }

    @Override
    public Integer getUserCount(Boolean countOutput, Integer start, Integer limit, String key, String value,
                                     Boolean search, Boolean searchByAny, String sortfield, String sorttype, String fields) {
        Document fieldsDoc = new Document();
        if (fields != null) {
            String[] fieldArray = fields.split(",");
            for (String field : fieldArray) {
                fieldsDoc.put(field, true);
            }
        }
        Query query = new BasicQuery(new Document(), fieldsDoc);
        // 查询条件处理
        if (searchByAny == null || !searchByAny) {
            if (search == null || !search) {
                if (key != null && value != null) {
                    String[] keys = key.split(",");
                    String[] values = value.split(",");
                    for (int i = 0; i < values.length; i++) {
                        Criteria criteria = Criteria.where(keys[i]).is(values[i]);
                        query.addCriteria(criteria);
                    }
                }
            } else {
                if (key != null && value != null) {
                    String[] keys = key.split(",");
                    String[] values = value.split(",");
                    for (int i = 0; i < values.length; i++) {
                        Criteria criteria = Criteria.where(keys[i]).regex(values[i]);
                        query.addCriteria(criteria);
                    }
                }
            }
        } else {
            if (search == null || !search) {
                if (key != null && value != null) {
                    String[] keys = key.split(",");
                    String[] values = value.split(",");
                    Criteria[] condition = new Criteria[keys.length];
                    for (int i = 0; i < values.length; i++) {
                        condition[i] = Criteria.where(keys[i]).is(values[i]);
                    }
                    Criteria criteria = new Criteria();
                    criteria.orOperator(condition);
                    query.addCriteria(criteria);
                }
            } else {
                if (key != null && value != null) {
                    String[] keys = key.split(",");
                    String[] values = value.split(",");
                    Criteria[] condition = new Criteria[keys.length];
                    for (int i = 0; i < values.length; i++) {
                        condition[i] = Criteria.where(keys[i]).regex(values[i]);
                    }
                    Criteria criteria = new Criteria();
                    criteria.orOperator(condition);
                    query.addCriteria(criteria);
                }
            }
        }
        if (sortfield != null && sorttype != null) {
            switch (sorttype) {
                case "ASC":
                    query.with(Sort.by(Sort.Order.asc(sortfield)));
                    break;
                case "DESC":
                    query.with(Sort.by(Sort.Order.desc(sortfield)));
                    break;
                default:
                    break;
            }
        }
        return (int)template.count(query, UserDetails.class);
    }

    @Override
    public UserDetails getUserDetails(String userId) {
        Query query = new Query(Criteria.where("_id").is(userId));
        return template.findOne(query, UserDetails.class);
    }

    @Override
    public void addUser(UserDetails user) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String date = format.format(new Date());
        user.setUserId(date);
        template.save(user);
    }

    @Override
    public void updateUser(UserDetails user) {
        Query query = new Query(Criteria.where("_id").is(user.getUserId()));
        Update update = new Update()
                .set("name", user.getName())
                .set("age", user.getAge())
                .set("code", user.getCode())
                .set("phone", user.getPhone())
                .set("apartment", user.getApartment())
                .set("bizLine", user.getBizLine())
                .set("email", user.getEmail())
                .set("entryTime", user.getEntryTime())
                .set("status", user.getStatus())
                .set("skill", user.getSkill());
        //更新查询返回结果集的第一条
        template.updateFirst(query, update, UserDetails.class);
    }

    @Override
    public void deleteUser(String... name) {
        Query query = new Query(Criteria.where("name").in(name));
        template.remove(query, UserDetails.class);
    }

    @Override
    public void test() {
        System.out.println("执行了Dao-test()");
        // 聚合操作groupBy的两种写法。 第一种未解决排序问题。
        /*GroupBy groupBy = GroupBy.key("code").initialDocument("{count:0}").reduceFunction("function(doc, out){out.count++}")
                .finalizeFunction("function(out){return out;}");
        GroupByResults<UserDetails> res = template.group("ums", groupBy, UserDetails.class);
        Document obj = res.getRawResults();
        System.out.println("===" + JSONObject.toJSONString(obj));*/

        /*Aggregation aggregation = Aggregation.newAggregation(Aggregation.group("code")
                .count().as("count"), Aggregation.sort(Sort.by(Sort.Order.desc("count"))));
        List<BasicDBObject> result = template.aggregate(aggregation, "ums", BasicDBObject.class).getMappedResults();
        for (Iterator<BasicDBObject> iterator = result.iterator(); iterator.hasNext();) {
            System.out.println("===" + JSONObject.toJSONString(iterator.next()));
        }*/

        Query query = new Query().addCriteria(Criteria.where("skill").is("B").andOperator(Criteria.where("skill").is("A")));
        List<UserDetails> result = template.find(query, UserDetails.class);
        for (UserDetails user : result) {
            System.out.println("===" + JSONObject.toJSONString(user));
        }
    }
}
