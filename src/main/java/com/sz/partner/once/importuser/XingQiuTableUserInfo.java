package com.sz.partner.once.importuser;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * todo sz※ EasyExcel 对于指定表头的Excel文件读取  ， @ExcelProperty指定要读取的Excel的表头名称，根据读取的Excel不同自定义进行匹配
 */
@Data
public class XingQiuTableUserInfo {

    /**
     * id
     */
    // @ExcelProperty("成员编号")
    @ExcelProperty("名称")
    private String planetCode;

    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;

}