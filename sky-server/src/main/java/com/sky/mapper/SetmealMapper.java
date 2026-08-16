package com.sky.mapper;

import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 套餐数据访问层
 */
@Mapper
public interface SetmealMapper {

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    List<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 新增套餐
     * @param setmeal
     */
    void save(Setmeal setmeal);


    void saveSetmealDishes(@Param("setmealDishes") List<SetmealDish> setmealDishes);

}
