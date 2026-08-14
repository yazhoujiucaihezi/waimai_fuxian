package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

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

    void save(DishDTO dishDTO);
}
