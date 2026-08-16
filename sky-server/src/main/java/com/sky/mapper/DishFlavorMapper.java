package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishFlavorMapper {
    void insert(DishFlavor flavor);

    void deleteByDishId(Long id);
}
