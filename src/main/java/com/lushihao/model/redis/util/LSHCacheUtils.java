package com.lushihao.model.redis.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 缓存提供类
 */
public class LSHCacheUtils {
    /**
     * 注入对象
     * 由于当前class不在spring boot框架内（不在web项目中）所以无法使用autowired，使用此种方法进行注入
     */
    private static RedisTemplate<String, String> template = (RedisTemplate<String, String>) LSHBeanUtils.getBean("redisTemplate");
    /**
     * 获取保存到Redis中时的时间
     */
    public static final Integer SAVE_TIME_30D = 60 * 60 * 24 * 30;
    /**
     * 默认时间
     */
    private static int time = 60 * 60 * 24 * 7;

    public static <T> boolean set(String key, T value) {
        return set(key, JSONObject.toJSONString(value), time);
    }

    public static boolean set(String key, String value, long validTime) {
        boolean result = template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = template.getStringSerializer();
                connection.set(serializer.serialize(key), serializer.serialize(value));
                connection.expire(serializer.serialize(key), validTime);
                return true;
            }
        });
        return result;
    }

    public static <T> T get(String key, Class<T> clazz) {
        return JSONObject.parseObject(get(key), clazz);
    }

    public static String get(String key) {
        String result = template.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = template.getStringSerializer();
                byte[] value = connection.get(serializer.serialize(key));
                return serializer.deserialize(value);
            }
        });
        return result;
    }

    public static <T> List<T> fuzzyGet(String key, Class<T> clazz) {
        List<String> values = fuzzyGet(key);
        List<T> tList = new ArrayList<>();
        for (String value : values) {
            tList.add(JSONObject.parseObject(value, clazz));
        }
        return tList;
    }

    public static List<String> fuzzyGet(String key) {
        List<String> result = template.execute(new RedisCallback<List<String>>() {
            @Override
            public List<String> doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = template.getStringSerializer();

                Set<String> keysList = template.keys("*" + key + "*");
                byte[][] keys = new byte[keysList.size()][];
                Iterator<String> it = keysList.iterator();
                int index = 0;
                while (it.hasNext()) {
                    keys[index++] = serializer.serialize(it.next());
                }

                List<byte[]> valueList = connection.mGet(keys);
                List<String> values = new ArrayList<>();
                for (byte[] value : valueList) {
                    if (value.length > 2) {
                        values.add(serializer.deserialize(value));
                    }
                }
                return values;
            }
        });
        return result;
    }

    public static boolean del(String key) {
        return template.delete(key);
    }
}
