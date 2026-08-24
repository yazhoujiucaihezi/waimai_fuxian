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
        DishVO dishVO = dishMapper.getDishById(dto.getDishId());
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCart.setUserId(BaseContext.getCurrentId());
        shoppingCart.setImage(dishVO.getImage());
        shoppingCart.setName(dishVO.getName());
        shoppingCart.setAmount(dishVO.getPrice());
        shoppingCart.setDishFlavor(dto.getDishFlavor());
        shoppingCart.setSetmealId(dto.getSetmealId());
        shoppingCart.setDishId(dto.getDishId());

        ShoppingCart sc = shoppingCartMapper.getByDishId(BaseContext.getCurrentId(), dto.getDishId());
        if(sc == null){
            shoppingCart.setNumber(1);
            shoppingCartMapper.add(shoppingCart);
        }else {
           shoppingCart.setNumber(sc.getNumber() + 1);
           shoppingCartMapper.update(shoppingCart);
        }

    }

    public List<ShoppingCart> list() {
        Long id = BaseContext.getCurrentId();
        return shoppingCartMapper.list(id);
    }

    public void sub(ShoppingCartDTO dto) {
        ShoppingCart shoppingCart = shoppingCartMapper.getByDishId(BaseContext.getCurrentId(), dto.getDishId());
        if(shoppingCart.getNumber() > 1){
            shoppingCart.setNumber(shoppingCart.getNumber() - 1);
            shoppingCartMapper.update(shoppingCart);
        }else {
            shoppingCartMapper.deleteById(BaseContext.getCurrentId(), dto.getDishId());
        }
    }

    public void clean() {
        shoppingCartMapper.clean(BaseContext.getCurrentId());
    }
}
