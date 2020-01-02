package com.lushihao.model.redis.util;

import com.alibaba.fastjson.JSONObject;
import com.lushihao.model.bean.util.LSHBeanUtils;
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
public class LSHRedisUtils {
    /**
     * 注入对象
     * 由于当前class不在spring boot框架内（不在web项目中）所以无法使用autowired，使用此种方法进行注入
     */
    private static RedisTemplate<String, String> template = (RedisTemplate<String, String>) LSHBeanUtils.getBean("redisTemplate");
    /**
     * 保存时间7天
     */
    private static final Long SAVE_TIME_7D = (long) 60 * 60 * 24 * 7;
    /**
     * 保存时间30天
     */
    public static final Long SAVE_TIME_30D = (long) 60 * 60 * 24 * 30;
    /**
     * 保存时间60天
     */
    private static final Long SAVE_TIME_60D = (long) 60 * 60 * 24 * 60;

    /**
     * 保存
     *
     * @param key       键
     * @param value     值
     * @param validTime 保存时间
     * @return
     */
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

    /**
     * 获取
     *
     * @param key   键
     * @param clazz 类型
     * @param <T>
     * @return
     */
    public static <T> T get(String key, Class<T> clazz) {
        String value = template.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer<String> serializer = template.getStringSerializer();
                byte[] value = connection.get(serializer.serialize(key));
                return serializer.deserialize(value);
            }
        });
        return JSONObject.parseObject(value, clazz);
    }

    /**
     * 模糊搜索
     *
     * @param key   键
     * @param clazz 类型
     * @param <T>
     * @return
     */
    public static <T> List<T> fuzzyGet(String key, Class<T> clazz) {
        List<String> values = template.execute(new RedisCallback<List<String>>() {
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
        List<T> tList = new ArrayList<>();
        for (String value : values) {
            tList.add(JSONObject.parseObject(value, clazz));
        }
        return tList;
    }

    /**
     * 删除
     *
     * @param key 键
     * @return
     */
    public static boolean del(String key) {
        return template.delete(key);
    }

    /**
     * 模糊删除
     *
     * @param key 键
     * @return
     */
    public static boolean fuzzyDel(String key) {
        Set<String> keysList = template.keys("*" + key + "*");
        long successCount = template.delete(keysList);
        if ((long) keysList.size() == successCount) {
            return true;
        } else {
            return false;
        }
    }
}
