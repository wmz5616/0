package com.zemcho.guzhe.util.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import jakarta.annotation.Resource;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @title: RedisUtil
 * @Description: redis助手工具
 * @Date: 2022/5/20 17:13
 */
@Component
public class RedisUtil {
    @Resource(name = "redisTemplate")
    RedisTemplate redisTemplate;

    /**
     * 根据key获取过期时间
     *
     * @param key 键:不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * 获取所有key
     *
     * @param prefix
     * @return
     */
    public Set<String> keys(String prefix) {
        try {
            return redisTemplate.keys(prefix);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 键:不能为null
     * @return true:存在；false:不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 键:可以传一个值或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 设置缓存
     *
     * @param key      键
     * @param value    值
     * @param time     时间:time要大于0,如果time小于等于0将设置无限期
     * @param timeUnit 时间单位
     * @return true:成功 false:失败
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 以增量方式存储long值（正值则自增，负值则自减）
     *
     * @param key
     * @param delta
     * @return
     */
    public boolean inc(String key, Long delta) {
        try {
            if (delta == null) {
                redisTemplate.opsForValue().increment(key);
            } else {
                redisTemplate.opsForValue().increment(key, delta);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将值插入到列表的尾部(最右边)
     *
     * @param key   键
     * @param value 值
     * @return true:成功 false:失败
     */
    public boolean rightPush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 批量将值插入到列表的尾部(最右边)
     *
     * @param key
     * @param values
     * @return
     */
    public boolean rightPushAll(String key, List values) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除并返回列表的第一个元素
     *
     * @param key 键
     * @return 值
     */
    public Object leftPop(String key) {
        return key == null ? null : redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 批量移除并返回列表的元素
     *
     * @param key
     * @param count
     * @return
     */
    public List batchLeftPop(String key, long count) {
        return key == null ? null : redisTemplate.opsForList().leftPop(key, count);
    }

    /**
     * 存储hash键值对
     *
     * @param key
     * @param map
     * @return
     */
    public boolean hashPutAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 存储单个键值对到hash
     *
     * @param key
     * @param hashKey
     * @param value
     * @return
     */
    public boolean hashPut(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断hash键值对是否存在
     *
     * @param key
     * @param hashKey
     * @return
     */
    public boolean hashHasKey(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().hasKey(key, hashKey);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取hash单个键值对
     *
     * @param key
     * @param hashKey
     * @return
     */
    public Object hashGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取hash全部键值对
     *
     * @param key
     * @return
     */
    public Map<String, Object> hashEntries(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除hash键值对
     *
     * @param key
     * @param hashKey
     * @return
     */
    public boolean hashDelete(String key, Object... hashKey) {
        try {
            redisTemplate.opsForHash().delete(key, hashKey);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 为指定key设置过期时间
     *
     * @param key      键
     * @param timeout  过期时间
     * @param timeUnit 时间单位
     * @return true: 成功；false: 失败
     */
    public  boolean expire(String key, long timeout, TimeUnit timeUnit) {
        try {
            return redisTemplate.expire(key, timeout, timeUnit) != null && redisTemplate.expire(key, timeout, timeUnit);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public  Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 向ZSet中添加成员和分数
     *
     * @param key   键
     * @param value 成员值
     * @param score 分数
     * @return true: 成功；false: 失败
     */
    public boolean zSetAdd(String key, Object value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 获取ZSet中某个成员的分数
     *
     * @param key   键
     * @param value 成员值
     * @return 分数
     */
    public Double zSetScore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 更新ZSet中某个成员的分数
     *
     * @param key   键
     * @param value 成员值
     * @param delta 要增加的分数（可正可负）
     * @return 新的分数
     */
    public Double zSetIncrementScore(String key, Object value, double delta) {
        try {
            return redisTemplate.opsForZSet().incrementScore(key, value, delta);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取ZSet中某个成员的排名（按分数从高到低）
     *
     * @param key   键
     * @param value 成员值
     * @return 排名（从0开始）
     */
    public Long zSetRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().rank(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取ZSet中指定区间的成员（按分数从高到低）
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引
     * @return 成员列表
     */
    public Set<Object> zSetRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取ZSet中指定分数范围内的成员
     *
     * @param key 键
     * @param min 最小分数(包含)
     * @param max 最大分数(包含)
     *            * @return 成员列表
     */
    public Set<Object> zSetRangeByScore(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取ZSet中所有成员（按分数从高到低）
     *
     * @param key 键
     * @return 所有成员集合
     */
    public Set<Object> zSetMembers(String key) {
        try {
            return redisTemplate.opsForZSet().range(key, 0, -1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取所有成员和分数
     *
     * @param key
     * @param isAsc
     * @return
     */
    public Map<String, Double> zSetGetAllAsMap(String key, Boolean isAsc) {
        try {
            Set<ZSetOperations.TypedTuple<String>> tuples = null;
            if (isAsc) { //按分数升序
                tuples = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
            } else { //按分数降序
                tuples = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
            }
            if (tuples == null) {
                return Collections.emptyMap();
            }

            Map<String, Double> result = new LinkedHashMap<>();
            for (ZSetOperations.TypedTuple<String> tuple : tuples) {
                result.put(String.valueOf(tuple.getValue()), tuple.getScore());
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    /**
     * 删除ZSet中的一个或多个成员
     *
     * @param key    键
     * @param values 成员数组
     * @return 删除数量
     */
    public Long zSetRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取ZSet中的最低的分数
     *
     * @param key 键
     * @return 删除数量
     */
    public Double zSetMinScore(String key) {
        try {
            Set<ZSetOperations.TypedTuple<Object>> tuples = redisTemplate.opsForZSet().rangeWithScores(key, 0, 0);
            if (tuples != null && !tuples.isEmpty()) {
                return tuples.iterator().next().getScore();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
