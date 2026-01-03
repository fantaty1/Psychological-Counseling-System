package com.cqust.psychologicalcounseling.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果封装类
 *
 * @param <T> 列表数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    
    /**
     * 数据列表
     */
    private List<T> list;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页码
     */
    private Integer page;
    
    /**
     * 每页数量
     */
    private Integer pageSize;
    
    /**
     * 创建分页结果
     *
     * @param list     数据列表
     * @param total    总记录数
     * @param page     当前页码
     * @param pageSize 每页数量
     * @param <T>      数据类型
     * @return PageResult
     */
    public static <T> PageResult<T> of(List<T> list, Long total, Integer page, Integer pageSize) {
        return new PageResult<>(list, total, page, pageSize);
    }
    
    /**
     * 创建分页结果（简化版）
     *
     * @param list  数据列表
     * @param total 总记录数
     * @param <T>   数据类型
     * @return PageResult
     */
    public static <T> PageResult<T> of(List<T> list, Long total) {
        return new PageResult<>(list, total, null, null);
    }
}
