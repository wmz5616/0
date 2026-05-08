package com.zemcho.guzhe.util.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * redis锁工具类
 *
 * @title: RedisLockUtil
 * @Description:
 * @Date: 2023/7/31 11:58
 */
@Component
public class RedisLockUtil {
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 获取锁
     *
     * @param lockKey 锁实例key
     * @return 锁信息
     */
    public RLock getRLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }

    /**
     * 加锁
     *
     * @param lockKey 锁实例key
     * @return 锁信息
     */
    public RLock lock(String lockKey) {
        RLock lock = getRLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     * 加锁
     *
     * @param lockKey   锁实例key
     * @param leaseTime 上锁后自动释放锁时间
     * @return true=成功；false=失败
     */
    public Boolean tryLock(String lockKey, long leaseTime) {
        return tryLock(lockKey, 0, leaseTime, TimeUnit.SECONDS);
    }

    /**
     * 加锁
     *
     * @param lockKey   锁实例key
     * @param leaseTime 上锁后自动释放锁时间
     * @param unit      时间颗粒度
     * @return true=加锁成功；false=加锁失败
     */
    public Boolean tryLock(String lockKey, long leaseTime, TimeUnit unit) {
        return tryLock(lockKey, 0, leaseTime, unit);
    }

    /**
     * 加锁
     *
     * @param lockKey   锁实例key
     * @param waitTime  最多等待时间
     * @param leaseTime 上锁后自动释放锁时间
     * @param unit      时间颗粒度
     * @return true=加锁成功；false=加锁失败
     */
    public Boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock rLock = getRLock(lockKey);
        boolean tryLock = false;
        try {
            tryLock = rLock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
        return tryLock;
    }

    /**
     * 释放锁
     *
     * @param lockKey 锁实例key
     */
    public void unlock(String lockKey) {
        RLock lock = getRLock(lockKey);
        if (lock.isLocked()) {
            lock.unlock();
        }
    }

    /**
     * 释放锁
     *
     * @param lock 锁信息
     */
    public void unlock(RLock lock) {
        lock.unlock();
    }

    /**
     * 判断指定的锁是否被某个线程持有
     *
     * @param lockKey 锁实例key
     * @return
     */
    public Boolean isLocked(String lockKey) {
        RLock lock = getRLock(lockKey);
        return lock.isLocked();
    }

    /**
     * 批量加锁
     *
     * @param prefix
     * @param ids
     * @param leaseTime
     * @param unit
     * @return
     */
    public Boolean tryLockMulti(String prefix, List<Integer> ids, long leaseTime, TimeUnit unit) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        List<Integer> lockSuccess = new ArrayList<>();
        try {
            for (Integer id : ids) {
                Boolean lockFlag = tryLock(prefix + id, leaseTime, unit);
                if (!lockFlag) {
                    throw new Exception("lock failed");
                }
                lockSuccess.add(id);
            }

            return true;
        } catch (Exception e) {
            releaseLockMulti(prefix, lockSuccess);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 批量释放锁
     *
     * @param prefix
     * @param ids
     */
    public void releaseLockMulti(String prefix, List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Integer id : ids) {
            unlock(prefix + id);
        }
    }
}
