package com.sz.partner.once.importuser;

import com.alibaba.excel.EasyExcel;

import java.util.List;

/**
 * 导入 Excel   分为知道具体表头的Excel，和不知道具体表头的Excel
 *

 */
public class ImportExcel {

    /**
     * 读取数据
     */
    public static void main(String[] args) {
        // todo 记得改为自己的测试文件
        // String fileName = "E:\\星球项目\\yupao-backend\\src\\main\\resources\\testExcel.xlsx";
        String fileName = "C:\\Users\\sz\\Downloads\\论文清单.xls";


//         todo sz※ 使用监听器方式读取，在读取时可以对数据进行获取  针对大数据量文件
//        readByListener(fileName);
 
        // todo sz※  使用同步读，使用EasyExcel框架读取excel 提高读取效率       针对小数据量文件
        synchronousRead(fileName);
    }

    /**
     * todo sz※ 监听器读取   适用于数据量比较大的情况，可以先读取到内存中，分批存入数据库中
     *
     * @param fileName
     */
    public static void readByListener(String fileName) {
        // 在读取时将监听器对应传入进去
        EasyExcel.read(fileName, XingQiuTableUserInfo.class, new TableListener()).sheet().doRead();
    }

    /**
     * todo sz※ 同步读，数据量大时会产生卡顿，有导致oom的风险。但是小数据量时快一些   使用head(数据对应类型实体);方法进行同步读，直接返回全部的数据     也可以配合监听器
     *
     * @param fileName
     */
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish

        // 使用EasyExcel框架的read方法读取excel,指定哪个类去读取，返回结果会被存储在指定类中
        List<XingQiuTableUserInfo> totalDataList =
                // EasyExcel.read(fileName).head(XingQiuTableUserInfo.class).sheet().doReadSync();
                EasyExcel.read(fileName,new TableListener()).head(XingQiuTableUserInfo.class).sheet().doReadSync();
        for (XingQiuTableUserInfo xingQiuTableUserInfo : totalDataList) {
            System.out.println(xingQiuTableUserInfo);
        }
    }

}
