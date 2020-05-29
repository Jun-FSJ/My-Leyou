package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author Jun
 * @create 2020/5/23 - 20:27
 */
@Service
public class UploadService {

    //fastDFs工具
    @Autowired
    private FastFileStorageClient storageClient;

    //定义一个List集合，存储要判断的文件类型集合
    private static final List<String> CONTENT_TYPES = Arrays.asList("image/jpeg","image/gif","image/png");

    //定义一个logg打印日志
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    /**
     * 实现图片文件上传
     * @param file
     * @return
     */
    public String uploadImage(MultipartFile file) {
        //1.首先检查上传的文件类型
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();//得到文件的类型
        if (!CONTENT_TYPES.contains(contentType)){//如果上传的文件类型不是这个集合中的
            LOGGER.info("上传的文件类型错误");
            return null;
        }
        //2.检查文件的内容
        try {
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (read == null){//读取文件的内容为空就证明这个文件不是图片文件
                LOGGER.info("上传的文件内容不是图片");
                return null;
            }
            //3.将上传的文件保存到服务器
//            file.transferTo(new File("E:\\Java\\idea-workspace\\myleyou\\images\\"+originalFilename));
            String ext = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");

            StorePath storePath = this.storageClient.uploadFile(
                    file.getInputStream(), file.getSize(), ext, null);

            //4.生成url地址，将其返回
            return "http://image.leyou.com/"+storePath.getFullPath();
        }catch (IOException e){
            LOGGER.info("服务器内部错误"+originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
