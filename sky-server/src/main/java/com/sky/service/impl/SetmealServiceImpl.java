package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 套餐业务层实现类
 */
@Service
@EnableCaching
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        List<SetmealVO> list = setmealMapper.page(setmealPageQueryDTO);
        Page<SetmealVO> page = (Page<SetmealVO>) list;
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增套餐
     * @param setmealDTO
     */
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public void save(SetmealDTO setmealDTO) {

        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setCreateUser(BaseContext.getCurrentId());
        setmeal.setUpdateTime(LocalDateTime.now());
        setmeal.setCreateTime(LocalDateTime.now());
        setmeal.setUpdateUser(BaseContext.getCurrentId());
        setmealMapper.save(setmeal);
        Long id = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(id);
            }

            System.out.println("setmealDishes = " + setmealDishes);

            setmealMapper.saveSetmealDishes(setmealDishes);
        }

    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> setmealDishes =
                setmealMapper.getSetmealDishesBySetmealId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐
     * @param setmealDTO
     */
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setUpdateUser(BaseContext.getCurrentId());
        setmeal.setUpdateTime(LocalDateTime.now());
        Long id = setmeal.getId();
        setmealMapper.update(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && setmealDishes.size() > 0) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(id);
            }
        }

        setmealMapper.deleteSetmealDishesBySetmealId(id);

        System.out.println("setmealDishes = " + setmealDishes);

        setmealMapper.saveSetmealDishes(setmealDishes);
    }

    /**
     * 修改套餐状态
     * @param status
     * @param id
     */
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public void updateStatus(Integer status, Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);
        setmeal.setUpdateUser(BaseContext.getCurrentId());
        setmeal.setUpdateTime(LocalDateTime.now());
        setmealMapper.update(setmeal);
    }

    /**
     * 删除套餐
     * @param ids
     */
    @CacheEvict(cacheNames = "setmeal", allEntries = true)
    public void delete(List<Long> ids) {
        for (Long id : ids){
            setmealMapper.deleteById(id);
        }
    }

    @Cacheable(cacheNames = "setmeal", key = "#p0.categoryId")
    /**
     * 查询套餐列表
     */
    public List<Setmeal> list(Setmeal setmeal) {

        List<Setmeal> setmealList = setmealMapper.list(setmeal);
        return setmealList;
    }


    public List<DishItemVO> getByDish(Long id) {
        return setmealMapper.getByDish(id);
    }
}
