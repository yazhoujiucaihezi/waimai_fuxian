package com.sky.mapper;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 分类数据访问层
 */
@Mapper
public interface CategoryMapper {

    /**
     * 新增分类
     * @param category
     */
    void insert(Category category);

    /**
     * 分类分页查询
     * @param dto
     * @return
     */
    List<Category> page(CategoryPageQueryDTO dto);

    /**
     * 根据id删除分类
     * @param id
     */
    void delete(Long id);

    /**
     * 修改分类
     * @param category
     */
    void update(Category category);

    /**
     * 根据类型查询分类
     * @param category
     * @return
     */
    List<Category> list(Category category);
}
