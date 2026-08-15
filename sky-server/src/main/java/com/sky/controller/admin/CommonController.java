package com.sky.controller.admin;

import com.sky.constant.FileUploadConstant;
import com.sky.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {


    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传,{}", file);
        //本地存储
        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //获取文件后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            //判断文件是否合法
            if(!extension.equals(".png") && !extension.equals(".jpg") && !extension.equals(".jpeg")){
                log.info("文件不合法 文件名为{}", originalFilename);
                return Result.error("文件不合法 重新上传！");
            }
            //生成新的文件名
            String newFileName = UUID.randomUUID().toString() + extension;
            //保存文件
            String path = FileUploadConstant.FILE_UPLOAD_PATH + newFileName;//文件存储路径
            file.transferTo(new File(path));
            return  Result.success(FileUploadConstant.FILE_VISIT_PATH + newFileName);
        } catch (IOException e) {
            log.error("文件上传失败！:{}",e);
            throw new RuntimeException(e);
        }
    }
}