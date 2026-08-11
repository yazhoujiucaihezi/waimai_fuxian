package com.sky.service.impl;


import com.sky.constant.MessageConstant;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.properties.JwtProperties;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

        claims.put("id", employee.getId());

        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims
        );


        return EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();
    }
}
