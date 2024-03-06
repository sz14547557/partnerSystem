package com.sz.partner.once.importuser;

import com.alibaba.excel.EasyExcel;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExportExcel {


    public static void main(String[] args) {

        // String fileName = TestFileUtil.getPath() + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        ExportExcel exportExcel = new ExportExcel();
        String fileName = exportExcel.getResourceName();
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, XingQiuTableUserInfo.class)
                .sheet("模板")
                .doWrite(() -> {
                    // 分页查询数据
                    XingQiuTableUserInfo xingQiuTableUserInfo = new XingQiuTableUserInfo();
                    xingQiuTableUserInfo.setUsername("sz");
                    xingQiuTableUserInfo.setPlanetCode("100");
                    List<XingQiuTableUserInfo> lists = new ArrayList<>();
                    lists.add(xingQiuTableUserInfo);
                    return lists;
                });

    }

    public String getResourceName(){

        FileSystemView fsv = FileSystemView.getFileSystemView();
        File com=fsv.getHomeDirectory();
        //桌面真实路径
        String Desktop = com.getPath();

        // 输出到target 的classes目录下的excel文档
        // return  this.getClass().getResource("/").getPath()+ "simpleWrite" + System.currentTimeMillis() + ".xlsx";

        // 输出到桌面
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(System.currentTimeMillis());
        return  Desktop+ "/easyExcelExpoty-" + format + ".xlsx";
    }
}
