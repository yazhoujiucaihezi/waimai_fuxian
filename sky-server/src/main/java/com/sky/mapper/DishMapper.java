package com.sky.mapper;

import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据id查询菜品
     */
    DishVO getDishById(Long id);
    /**
     * 分类分页查询
     * @param dto
     * @return
     */
    List<DishVO> page(DishPageQueryDTO dto);

    void insert(Dish dish);

    void update(Dish dish);

    void deleteById(Long id);

    List<DishVO> list(Long categoryId,Integer status);
}
