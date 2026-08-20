package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/dish")
@Slf4j
public class UDishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {

        String key = "dish" + categoryId;
        log.info("用户查询菜品列表 {}", categoryId);

        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (dishVOList != null) {
            return Result.success(dishVOList);
        }

        List<DishVO> list = dishService.list(categoryId);
        redisTemplate.opsForValue().set(key, list);
        return Result.success(list);
    }
}
