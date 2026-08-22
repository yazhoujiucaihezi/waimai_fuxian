package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ShoppingCartserviceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;

    public void add(ShoppingCartDTO dto) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(dto, shoppingCart);
        DishVO dishVO = dishMapper.getDishById(dto.getDishId());
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCart.setImage(dishVO.getImage());
        shoppingCart.setName(dishVO.getName());
        shoppingCart.setAmount(dishVO.getPrice());
        if(shoppingCart.getNumber() == null){
            shoppingCart.setNumber(1);
        }else {
            Integer number = shoppingCart.getNumber();
            shoppingCart.setNumber(number + 1);
        }
        shoppingCartMapper.add(shoppingCart);
    }

    public List<ShoppingCart> list() {
        Long id = BaseContext.getCurrentId();
        return shoppingCartMapper.list(id);
    }
}
