package com.sky.controller.admin;

import com.github.pagehelper.Constant;
import com.github.pagehelper.PageHelper;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     * @param employeeLoginDTO
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO){
        log.info("员工登录：{}", employeeLoginDTO);
        EmployeeLoginVO vo = employeeService.login(employeeLoginDTO);
        return Result.success(vo);
    }

    /**
     * 员工分页查询
     * @param pageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO pageQueryDTO)
    {
        log.info("分页查询员工：{}", pageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(pageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 员工退出登录
     * @return
     */
    @PostMapping("/logout")
    public Result logout()
    {
        log.info("员工退出登录");
        employeeService.logout();
        return Result.success();
    }

    /**
     * 新增员工
     * @param dto
     * @return
     */
    @PostMapping
    public Result save(@RequestBody EmployeeDTO dto){
        log.info("新增员工 {}",dto);
        employeeService.save(dto);
        return Result.success();
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工：{}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 编辑员工信息
     * @param dto
     * @return
     */
    @PutMapping
    public Result update(@RequestBody EmployeeDTO dto){
        log.info("编辑员工信息: {}",dto);
        employeeService.update(dto);
        return Result.success();
    }
}
