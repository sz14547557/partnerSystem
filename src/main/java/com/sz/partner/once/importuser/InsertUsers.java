package com.sz.partner.once.importuser;

import com.sz.partner.mapper.UserMapper;
import com.sz.partner.model.domain.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * 导入用户任务
 *

 */
@Component
public class InsertUsers {

    @Resource
    private UserMapper userMapper;
    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "sz";

    /**
     * 批量插入用户
     * 测试定时任务操作 initialDelay每隔5秒执行一次  fixedRate 隔多少秒之后再次执行。如果定义为最大值，则表示只执行一次
     */
   // @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    public void doInsertUsers() {
        // todo sz※ StopWatch 方法测试代码执行时间
        StopWatch stopWatch = new StopWatch();
        System.out.println("goodgoodgood");
        // 开启计时
        stopWatch.start();
        final int INSERT_NUM = 1000;

        // for循环插入数据慢的问题：
        // 1.每次插入都要建立数据库连接，并关闭连接
        // 2.for循环是线性的，上一次循环未执行，则需要等待

        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("某政");
            user.setUserAccount("14547557"+i);
            user.setAvatarUrl("https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png");
            user.setGender(0);
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + "12345678").getBytes());
            user.setUserPassword(encryptPassword);
            user.setPhone("17645359817");
            user.setEmail("123@qq.com");
            user.setTags("[男]");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode(String.valueOf(i));
            userMapper.insert(user);
        }
        // 结束计时
        stopWatch.stop();
        // 输出执行时间
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}
