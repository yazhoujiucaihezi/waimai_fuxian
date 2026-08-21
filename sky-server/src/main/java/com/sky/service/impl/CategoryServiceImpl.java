package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类业务层实现类
 */
@Service
@Slf4j
@EnableCaching
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    /**
     * 新增分类
     */
    @CacheEvict(cacheNames = "category", allEntries = true)
    public void add(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.DISABLE);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.insert(category);
    }

    /**
     * 分类分页查询
     *
     * @param dto
     * @return
     */
    public PageResult page(CategoryPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(),dto.getPageSize());
        List<Category> list = categoryMapper.page(dto);

        Page<Category> page = (Page<Category>) list;
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id删除分类
     * @param id
     */
    @CacheEvict(cacheNames = "category", allEntries = true)
    public void delete(Long id) {
        categoryMapper.delete(id);
    }

    /**
     * 修改分类
     */
    @CacheEvict(cacheNames = "category", allEntries = true)
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.update(category);
    }

    /**
     * 修改分类状态
     */
    @CacheEvict(cacheNames = "category", allEntries = true)
    public void updateStatus(Integer status, Long id) {
        Category category = new Category();
        category.setStatus(status);
        category.setId(id);
        categoryMapper.update(category);
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @Cacheable(cacheNames = "category",  key = "'category'")
    public List<Category> list(Integer type) {
        Category category = new Category();
        category.setType(type);
        return categoryMapper.list(category);
    }
}
