package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    void add(ShoppingCart shoppingCart);

    List<ShoppingCart> list(Long userId);

    ShoppingCart getByDishId(Long userId, Long dishId);

    void update(ShoppingCart shoppingCart);

    void deleteByDishId(Long userId, Long dishId);

    void clean(Long currentId);

    ShoppingCart getBySetmealId(Long currentId, Long setmealId);

    void deleteBySetmealId(Long currentId, Long setmealId);
}
