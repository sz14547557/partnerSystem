package com.sz.partner.once.importuser;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.extern.slf4j.Slf4j;

/**
 * Excel 读取监听  EasyExcel 读取时监听器，重写ReadListener中定义的方法，就可以在读取操作时对应的数据
 *
 * 使用监听器，需要将监听器绑定到需要监听的地方

 */
@Slf4j
public class TableListener implements ReadListener<XingQiuTableUserInfo> {

    /**
     * 这个每一条数据解析都会来调用  每读到一次数据，会调用一次当前方法，参数为当前读到的数据，和上下文信息
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(XingQiuTableUserInfo data, AnalysisContext context) {
        // 在此处可以将读取的数据存入内存，在满足条数时保存到数据库，并清除内存中的数据。防止内存溢出oom
        System.out.println("我是获取的数据："+data);
    }

    /**
     * 所有数据解析完成,最后一条数据解析完成后，会调用这个方法
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        System.out.println("已解析完成");
    }
}