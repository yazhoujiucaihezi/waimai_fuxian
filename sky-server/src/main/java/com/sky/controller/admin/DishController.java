package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.result.Result;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 分类分页查询
     * @param dto
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dto){
        log.info("分类分页查询 {}",dto);
        PageResult page = dishService.page(dto);
        return Result.success(page);
    }

    /**
     * 根据id查询菜品
     */
    @GetMapping("/{id}")
    public Result<DishVO> getDishById(@PathVariable Long id) {
        log.info("根据id查询菜品");
        DishVO dishVO = dishService.getDishById(id);
        return Result.success(dishVO);
    }

    /**
     * 新增菜品
     */
    @PostMapping
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.save(dishDTO);
        return Result.success();
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public Result update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.update(dishDTO);
        return Result.success();
    }

    /**
     * 批量删除菜品
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除菜品：{}", ids);
        dishService.delete(ids);
        return Result.success();
    }

    /**
     * 修改菜品状态
     */
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,@RequestParam Long id){
        log.info("修改菜品状态：{}", status);
        dishService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品列表
     */
    @GetMapping("/list")
    public Result<List<DishVO>> list(@RequestParam Long categoryId, @RequestParam Integer status) {
        log.info("根据分类id查询菜品列表");
        List<DishVO> list = dishService.list(categoryId, status);
        return Result.success(list);
    }
}
