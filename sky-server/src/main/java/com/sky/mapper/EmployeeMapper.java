package com.sky.mapper;


import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUserName(String username);

    /**
     * 员工分页查询
     * @param pageQueryDTO
     * @return
     */
    List<Employee> pageQuery(EmployeePageQueryDTO pageQueryDTO);

    /**
     * 新增员工
     * @param employee
     */
    void insert(Employee employee);

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);

    /**
     * 编辑员工信息
     * @param employee
     * @return
     */
    void update(Employee employee);

    /**
     * 启用禁用员工账号
     * @param id
     */
    void startOrStop(Long id, Integer status);
}
