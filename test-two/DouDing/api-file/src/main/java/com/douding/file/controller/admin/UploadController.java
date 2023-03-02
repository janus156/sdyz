package com.douding.file.controller.admin;

import com.douding.server.domain.Test;
import com.douding.server.dto.FileDto;
import com.douding.server.dto.ResponseDto;
import com.douding.server.enums.FileUseEnum;
import com.douding.server.service.FileService;
import com.douding.server.service.TestService;
import com.douding.server.util.Base64ToMultipartFile;
import com.douding.server.util.UuidUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/*
    返回json 应用@RestController
    返回页面  用用@Controller
 */
@RequestMapping("/admin/file")
@RestController
public class UploadController {

    private static final Logger LOG = LoggerFactory.getLogger(UploadController.class);
    public  static final String BUSINESS_NAME ="文件上传";
    @Resource
    private TestService testService;

    @Value("${file.path}")
    private String FILE_PATH;

    @Value("${file.domain}")
    private String FILE_DOMAIN;

    @Resource
    private FileService fileService;

    @RequestMapping("/upload")
    public ResponseDto upload(@RequestBody FileDto fileDto) throws Exception {
        String use = fileDto.getUse();
        //路径
        String filePath=fileDto.getPath();
        String key = fileDto.getKey();
        String suffix = fileDto.getSuffix();
        String dtoShard = fileDto.getShard();
        //Base64转multipart
        MultipartFile shard = Base64ToMultipartFile.base64ToMultipart(dtoShard);
        FileUseEnum useEnum = FileUseEnum.getByCode(use);
        //dir小写
        String dir = useEnum.name().toLowerCase();
        File fullDir = new File(FILE_PATH + dir);

        if (!fullDir.exists()) {
            fullDir.mkdirs();
        }

        // 路径
        String path = new StringBuffer(dir)
                .append("/")
                .append(key)
                .append(".")
                .append(suffix)
                .toString();
        // teacher/6sfSqfOwzmik4A4icMYuUe.mp4.1
        String localPath = new StringBuffer(path)
                .append(".")
                .append(fileDto.getShardIndex())
                .toString();
        String partPath = FILE_PATH + localPath;
        String partPath1 = FILE_PATH + path;
        String fullPath = FILE_DOMAIN+path;
        ResponseDto res = new ResponseDto();
        File dest = new File(partPath);
        try {
            // 保存文件
            shard.transferTo(dest);
        } catch (IOException e) {
            LOG.error(e.getMessage());
            ResponseDto<Object> ress = new ResponseDto<>();
            ress.setSuccess(false);
            return ress;
        }
        fileDto.setPath(fullPath);
        res.setContent(fileDto);
        return res;
    }

    //合并分片
    public void merge(FileDto fileDto) throws Exception {
        LOG.info("合并分片开始");

    }

    @GetMapping("/check/{key}")
    public ResponseDto check(@PathVariable String key) throws Exception {
        LOG.info("检查上传分片开始：{}", key);

        return null;
    }

}//end class
