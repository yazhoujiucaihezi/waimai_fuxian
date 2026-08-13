package com.sky.mapper;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {


    void insert(Category category);

    List<Category> page(CategoryPageQueryDTO dto);

    void delete(Long id);

    void update(Category category);

}
