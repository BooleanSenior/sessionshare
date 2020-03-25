package com.example.sessionshare.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;
@Component
public class RedisUtils {

    public  static Long EXPIRE = Long.valueOf(60*5);  //设置时间


    @Autowired
    private StringRedisTemplate redisTemplate;



    /**  根据key存缓存
     *根据key - value插入缓存
     * @param key: 具体的key
     * @param value: 具体的值
     * @param time: 缓存过期时间
     * @param timeUnit: 缓存过期时间单位  例如天  TimeUnit.DAYS
     * */
    public void setKey(String key, Object value, Long time, TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key, value.toString(), time, timeUnit);
    }


    /** 取缓存：
     *根据key获取缓存
     * */
    public String getKey(String key){
        return redisTemplate.opsForValue().get(key);
    }
    /**
     * 判断key是否存在
     * */
    public boolean exitst(String key){
        return redisTemplate.hasKey(key);
    }


    /**
     * 根据key删除
     * */
    public  void remove(String key){
        if (exitst(key)){
            redisTemplate.delete(key);
        }
    }


    /** 缓存value增加，适用于做统计用户浏览量，
     * 缓存value自增
     * */
    public Long increment(String key, Long num){
        return redisTemplate.opsForValue().increment(key, num);
    }


    /**
     * 利用redis去重：
     * */
    public void setBitmap(String key, Long uid){
        redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                connection.setBit( ( (RedisSerializer< String >)redisTemplate.getKeySerializer() ).serialize( key ), uid, true );
                return true;
            }
        });
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }


    /**
     * 统计
     * */
    public Long bitCount(String key){
        Long count = redisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount( ( (RedisSerializer< String >)redisTemplate.getKeySerializer() ).serialize( key ));
            }
        });
        return count;
    }



    public void hPutAll(String key, Map<String, String> maps) {
        redisTemplate.opsForHash().putAll(key, maps);
    }

}
