package com.tanhua.dubbo.api.mongo;

import com.tanhua.domain.mongo.UserLocation;
import com.tanhua.dubbo.api.mong.BaiduApi;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Service
public class BaiduApiImpl implements BaiduApi {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 保存地理位置
     * @param latitude  //纬度
     * @param longitude//经度
     * @param addrStr//位置描述
     */
    @Override
    public void upLocation(Double latitude, Double longitude, String addrStr,Long currentUserID) {
        //封装对象存储
        UserLocation userLocation = new UserLocation();


        //a 先查询该用户有没有地理位置记录
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(currentUserID));
        UserLocation one = mongoTemplate.findOne(query, UserLocation.class);

        //获取当前时间
        long timeMillis = System.currentTimeMillis();
        if(null != one){

            //b 如果存在就更新当前位置
            Update update = new Update();
            update.set("address",addrStr).set("location",new GeoJsonPoint(latitude,longitude))
                    .set("updated",timeMillis).set("lastUpdated",one.getUpdated());
            mongoTemplate.updateFirst(query,update,UserLocation.class);
        }else{
            //c 如果不存在就添加用户记录
            userLocation.setId(ObjectId.get());
            userLocation.setUserId(currentUserID);  //当前用户id
            userLocation.setAddress(addrStr);       //位置信息
            userLocation.setLocation(new GeoJsonPoint(latitude,longitude));//经度纬度
            userLocation.setCreated(timeMillis);    //创建时间
            userLocation.setUpdated(timeMillis);     //更新时间
            userLocation.setLastUpdated(timeMillis);  //上一次更新时间
            mongoTemplate.insert(userLocation);
        }

    }
}
