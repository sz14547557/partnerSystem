package com.sz.partner.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redisson 测试  Redisson是一个操作Redis的本地客户端
 *

 */
@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test() {

        // RBucket<Object> sz = redissonClient.getBucket("sz");
        // System.out.println( sz.get());

        RKeys keys = redissonClient.getKeys();
        keys.getKeysStream().forEach(key -> System.out.println(key));
        // list，数据存在本地 JVM 内存中
        List<String> list = new ArrayList<>();
        list.add("yupi");
        System.out.println("list:" + list.get(0));

        list.remove(0);

        //todo sz※  使用redisson操作集合  与java操作集合的操作是一样的 add get(0) remove(0)

        // todo sz※ 但是我这边无法操作本地的redis
        // 数据存在 redis 的内存中  直接获取 类型为list，名称为test-list的 数据 ，通过add方法直接向redis中添加数据
        RList<String> rList = redissonClient.getList("test-list");
        rList.add("sz666");
        System.out.println("rlist:" + rList.get(0));
        rList.remove(0);

        // map
        Map<String, Integer> map = new HashMap<>();
        map.put("sz1", 10);
        map.get("sz2");

        RMap<Object, Object> map1 = redissonClient.getMap("test-map");

        // set

        // stack


    }

    @Test
    void testWatchDog() {
        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                Thread.sleep(300000);
                System.out.println("getLock: " + Thread.currentThread().getId());
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
