package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
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
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     */
    public void add(ShoppingCartDTO dto) {

        //套餐
        if(dto.getSetmealId() != null && dto.getSetmealId() != 0){

            ShoppingCart shoppingCart = new ShoppingCart();

            shoppingCart.setSetmealId(dto.getSetmealId());

            Setmeal setmeal = setmealMapper.getById(dto.getSetmealId());

            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());

            shoppingCart.setNumber(1);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());


            ShoppingCart sc = shoppingCartMapper.getBySetmealId(
                    BaseContext.getCurrentId(),
                    dto.getSetmealId()
            );

            if(sc == null){
                shoppingCartMapper.add(shoppingCart);
            }else {
                sc.setNumber(sc.getNumber() + 1);
                shoppingCartMapper.update(sc);
            }

        }

        //菜品
        else {
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setDishId(dto.getDishId());
            DishVO dishVO = dishMapper.getDishById(dto.getDishId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setImage(dishVO.getImage());
            shoppingCart.setName(dishVO.getName());
            shoppingCart.setAmount(dishVO.getPrice());
            shoppingCart.setDishFlavor(dto.getDishFlavor());
            ShoppingCart sc = shoppingCartMapper.getByDishId(BaseContext.getCurrentId(), dto.getDishId());
            if(sc == null){
                shoppingCart.setNumber(1);
                shoppingCartMapper.add(shoppingCart);
            }
            else {
                sc.setNumber(sc.getNumber() + 1);
                shoppingCartMapper.update(sc);
            }
        }

    }

    /**
     * 购物车列表
     */
    public List<ShoppingCart> list() {
        Long id = BaseContext.getCurrentId();
        return shoppingCartMapper.list(id);
    }

    /**
     * 减少购物车数量
     */
    public void sub(ShoppingCartDTO dto) {


        // 套餐
        if (dto.getSetmealId() != null && dto.getSetmealId() != 0){
            ShoppingCart sc = shoppingCartMapper.getBySetmealId(BaseContext.getCurrentId(), dto.getSetmealId());

            if (sc.getNumber() > 1) {
                sc.setNumber(sc.getNumber() - 1);
                shoppingCartMapper.update(sc);
            } else {
                shoppingCartMapper.deleteBySetmealId(BaseContext.getCurrentId(), dto.getSetmealId());
            }
        }

        // 菜品
        else {
            ShoppingCart shoppingCart = shoppingCartMapper.getByDishId(BaseContext.getCurrentId(), dto.getDishId());

            if (shoppingCart.getNumber() > 1) {
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.update(shoppingCart);
            } else {
                shoppingCartMapper.deleteByDishId(BaseContext.getCurrentId(), dto.getDishId());
            }
        }
    }

    /**
     * 清空购物车
     */
    public void clean() {
        shoppingCartMapper.clean(BaseContext.getCurrentId());
    }
}
