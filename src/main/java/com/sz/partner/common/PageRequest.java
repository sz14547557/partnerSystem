package com.sz.partner.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数  定义一个通用的分页查询请求，对单个模块继承当前请求参数，自定义分页参数，添加默认值，防止前端传递为空时导致的问题。
 * 默认第一页，大小为10
 */
@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = -5860707094194210842L;

    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前是第几页
     */
    protected int pageNum = 1;

}
