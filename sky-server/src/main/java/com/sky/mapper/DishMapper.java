package com.sky.mapper;

import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 菜品数据访问层
 */
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

    /**
     * 新增菜品
     * @param dish
     */
    void insert(Dish dish);

    /**
     * 修改菜品
     * @param dish
     */
    void update(Dish dish);

    /**
     * 根据id删除菜品
     * @param id
     */
    void deleteById(Long id);

    /**
     * 根据分类id查询菜品列表
     *
     * @param categoryId
     * @return
     */
    List<DishVO> list(Long categoryId);
}
