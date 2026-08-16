package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品口味数据访问层
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 新增菜品口味
     * @param flavor
     */
    void insert(DishFlavor flavor);

    /**
     * 根据菜品id删除口味
     * @param id
     */
    void deleteByDishId(Long id);
}
