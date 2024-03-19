package com.sz.partner.service;

import com.sz.partner.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 导入用户测试
 *

 */
@SpringBootTest
public class InsertUsersTest {

    @Resource
    private UserService userService;

    // 修改线程池参数，不使用默认的线程池

    // Cpu密集型 和 IO密集型
    private ExecutorService executorService = new ThreadPoolExecutor(40, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "sz";
    /**
     * 批量插入用户（异步+批处理）   for循环插入数据慢的问题：
     *                  1.每次插入都要建立数据库连接，并关闭连接    使用Batch批处理方式处理数据
     *                  2.for循环是线性的，上一次循环未执行，则需要等待  使用异步方式
     */
    @Test
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("某政"+i);
            user.setUserAccount("14547557"+i);
            user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
            user.setGender(0);
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + "12345678").getBytes());
            user.setUserPassword(encryptPassword);
            user.setPhone("123");
            user.setEmail("123@qq.com");
            user.setTags("[]");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("11111111");
            userList.add(user);
        }
        // 20 秒 10 万条   使用批量方式Batch进行处理,表示将这些数据批量处理，在插入时只开启一次SqlSession
        userService.saveBatch(userList, 10000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 并发批量插入用户        将总数据进行分组，每组多少条。分条进行执行  理解一下这个异步插入数据操作
     */
    @Test
    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 一共插入5000*100  500000条数据    5000为一组执行
        // 分一百组？
        int batchSize = 5000;

        // 提取出来已插入的条数
        int j = 0;

        // 用并发提高插入效率 通过外层的j进行控制插入的数据条数。
        // 针对于插入的数据没有顺序，可以使用这种方式。如果需要控制并发的顺序，不能使用这种方式
        List<CompletableFuture<Void>> futureList = new CopyOnWriteArrayList<>();
        for (int i = 0; i < 100; i++) {
            // 集合需要使用安全集合
            List<User> userList = new CopyOnWriteArrayList<>();
            // 这个while循环 有讲究
            while (true) {
                j++;
                User user = new User();
                user.setUsername("某政");
                user.setUserAccount("14547557"+i);
                user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
                user.setGender(0);
                user.setUserPassword("12345678");
                user.setPhone("123");
                user.setEmail("123@qq.com");
                user.setTags("[男]");
                user.setUserStatus(0);
                user.setUserRole(0);
                user.setPlanetCode(String.valueOf(i));
                userList.add(user);

                // 满足5000一组，则中断处理
                if (j % batchSize == 0) {
                    break;
                }
            }
            // 异步执行     开100个线程去执行 每个线程批处理5000条数据到数据库
            // 1.每次插入都要建立数据库连接，并关闭连接    使用Batch批处理方式处理数据
            // 2.for循环是线性的，上一次循环未执行，则需要等待  使用异步操作批处理
            // CompletableFuture.runAsync 方法 新建一个异步的任务，并指定线程池
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            }, executorService);

            // 将100次线程循环处理都放在futureList中
            futureList.add(future);
        }


        // 直到异步任务全部执行完毕，才会执行下一条语句
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        // 50 万条
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
