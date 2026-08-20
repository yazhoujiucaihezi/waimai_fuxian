package com.sky.mapper;

import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.vo.DishItemVO;
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

    /**
     * 批量保存套餐和菜品的关联关系
     * @param setmealDishes
     */
    void saveSetmealDishes(@Param("setmealDishes") List<SetmealDish> setmealDishes);

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    Setmeal getById(Long id);

    /**
     * 修改套餐
     * @param setmeal
     */
    void update(Setmeal setmeal);

    /**
     * 根据套餐id查询套餐和菜品的关联关系
     * @param id
     * @return
     */
    List<SetmealDish> getSetmealDishesBySetmealId(Long id);

    /**
     * 根据套餐id删除套餐和菜品的关联关系
     * @param id
     */
    void deleteSetmealDishesBySetmealId(Long id);

    /**
     * 根据id删除套餐
     * @param id
     */
    void deleteById(Long id);

    List<Setmeal> list(Setmeal setmeal);

    List<DishItemVO> getByDish(Long id);
}
