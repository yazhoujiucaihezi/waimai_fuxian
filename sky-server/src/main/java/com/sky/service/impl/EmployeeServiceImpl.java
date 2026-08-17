package com.sky.service.impl;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.PasswordConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordEditFailedException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工业务层实现类
 */
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private JwtProperties jwtProperties;
    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    public EmployeeLoginVO login(EmployeeLoginDTO employeeLoginDTO) {

        //根据用户名查询员工
        Employee employee = employeeMapper.getByUserName(employeeLoginDTO.getUsername());

        //判断员工是否存在
        if(employee == null){
            throw new AccountNotFoundException();
        }

        //判断密码是否匹配
        if(!employee.getPassword().equals(DigestUtils.md5DigestAsHex(employeeLoginDTO.getPassword().getBytes()))){
            throw new PasswordErrorException();
        }

        //判断账号是否被锁定
        if(employee.getStatus() == 0){
            throw new AccountLockedException();
        }

        //生成JWT
        Map<String, Object> claims = new HashMap<>();

        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());

        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims
        );
        System.out.println("token = " + token);


        return EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();
    }

    /**
     * 员工分页查询
     * @param pageQueryDTO
     * @return
     */
    public PageResult pageQuery(EmployeePageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(),pageQueryDTO.getPageSize());
        List<Employee> list = employeeMapper.pageQuery(pageQueryDTO);

        Page<Employee> page = (Page<Employee>) list;
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 员工退出登录
     */
    public void logout() {

    }

    /**
     * 新增员工
     * @param dto
     */
    public void save(EmployeeDTO dto) {
       Employee employee = new Employee();

       BeanUtils.copyProperties(dto, employee);

       employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

       employee.setStatus(0);

       employeeMapper.insert(employee);
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    public Employee getById(Long id) {
         Employee employee = employeeMapper.getById(id);
         return employee;
    }

    /**
     * 编辑员工信息
     * @param dto
     * @return
     */
    public void update(EmployeeDTO dto) {

        Employee employee = new Employee();

        BeanUtils.copyProperties(dto,employee);

        employeeMapper.update(employee);
    }

    /**
     * 启用禁用员工账号
     * @param id
     */
    public void startOrStop(Long id, Integer status) {
        employeeMapper.startOrStop(id, status);
    }

    /**
     * 修改密码
     * @param passwordEditDTO
     */
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        String oldPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getOldPassword().getBytes());
        Long id = BaseContext.getCurrentId();
        Employee employee = employeeMapper.getById(id);

        //判断旧密码是否正确
        if(!employee.getPassword().equals(oldPassword)){
            throw new PasswordErrorException("原密码不正确");
        }

        String newPassword = DigestUtils.md5DigestAsHex(passwordEditDTO.getNewPassword().getBytes());

        //判断新密码是否和旧密码重合
        if(newPassword.equals(oldPassword)){
            throw new PasswordEditFailedException("新密码不能与旧密码相同");
        }

        employee.setPassword(newPassword);

        employeeMapper.update(employee);
    }
}
