package com.sky.mapper;

import com.sky.context.BaseContext;
import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    void add(ShoppingCart shoppingCart);

    List<ShoppingCart> list(Long userId);
}
