package com.sz.partner.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sz.partner.model.domain.User;
import com.sz.partner.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务  通过定时任务实现缓存预热，保证用户在查询时会一直命中缓存
 *

 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    // 如果用户数量太多，则对重点用户进行预热
    private List<Long> mainUserList = Arrays.asList(1L);

    // 每天执行，预热推荐用户  将全部用户查询出来插入到Redis的缓存中完成缓存预热
    // 在分布式系统下，控制定时任务在同一时间，只能有一个执行。防止重复执行导致的问题，所以需要加锁

    // 使用synchronized关键字就可以保证线程安全吗？
    // synchronized关键字只能在单个Jvm中保证线程安全，在分布式系统中（多个JVM）无法保证线程安全

    // Java在单机情况下，什么时候需要使用锁？
    // 单个服务器如果使用多线程，在保证线程按顺序执行时，需要使用锁


    // Java在分布式系统下执行定时任务，如何保证只有一个系统执行定时任务呢？
    // 1.将定时任务部署在单一的系统中，只有一个系统执行定时任务。
    // 2.在代码中进行判断，只有指定ip的服务器才执行定时任务（ip动态改变会导致问题）。写死配置，在并发量不大的时候
    // 3.在第二步的基础上，将写死的配置改为动态配置。在外部不需重新部署代码，直接修改配置
    // 4.使用分布式锁，不需要动态配置。只有有限资源的情况下，同一时间段，控制哪些线程可以获取资源
    @Scheduled(cron = "0 31 0 * * *")
    public void doCacheRecommendUser() {


        // Java中分布式锁如何实现？
        // 1.Mysql使用for update，在数据库层面进行加锁
        // 2.使用Redisson    底层使用setnx 命令实现
        // 3.Zookeeper实现


        // 分布式锁，定义同一时间只有一个服务器才能获取锁

        // Java的分布式锁的实现原理是什么？
        // 实现：先来的人获取资源，将资源进行标识。后来的人发现资源已被标识，则进行等待。直到先来的人执行方法结束，将资源标识清空。其他的服务器继续抢锁

        // Java使用分布式锁需要注意什么？
        // 1.分布式锁一定要释放掉，谁加的锁，谁就必须释放掉
        // 2.分布式锁一定要添加过期时间
        // 3.防止分布式锁的过期时间小于方法的执行时间，从而导致释放掉了别人加的分布式锁+同时执行。方法要续期
        // 4.释放分布式锁的时候，A已经判断是自己的分布式锁，但是此时正好A分布式锁过期了（还没执行A分布式锁的释放操作），此时其他人再次加B分布式锁，A继续执行释放操作，导致B的分布式锁被释放掉。需要使用Redis的原子操作解决这个问题

        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                System.out.println("getLock: " + Thread.currentThread().getId());
                for (Long userId : mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("yupao:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    // 写缓存
                    try {
                        valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}
