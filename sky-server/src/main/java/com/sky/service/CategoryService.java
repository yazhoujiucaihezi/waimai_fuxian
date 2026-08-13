package com.sky.service;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

public interface CategoryService {

    /**
     * 新增分类
     */
    void add(CategoryDTO categoryDTO);

    /**
     * 分类分页查询
     *
     * @param dto
     * @return
     */
    PageResult page(CategoryPageQueryDTO dto);

    /**
     * 删除分类
     * @param id
     */
    void delete(Long id);

    /**
     * 修改分类
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 修改分类状态
     */
    void updateStatus(Integer status, Long id);
}
