package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 根据id查询菜品
     */
    DishVO getDishById(Long id);
    /**
     * 分类分页查询
     * @param dto
     * @return
     */
    PageResult page(DishPageQueryDTO dto);

    /**
     * 新增菜品
     */
    void save(DishDTO dishDTO);

    /**
     * 修改菜品
     */
    void update(DishDTO dishDTO);

    /**
     * 批量删除菜品
     */
    void delete(List<Long> ids);

    /**
     * 修改菜品状态
     */
    void startOrStop(Integer status, Long id);

    List<DishVO> list(Long categoryId, Integer status);
}
