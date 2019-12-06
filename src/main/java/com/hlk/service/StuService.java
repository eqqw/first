package com.hlk.service;

import com.hlk.pojo.Stu;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * Created by lenovo on 2017/4/25.
 */
public interface StuService {
     void addStu(Stu stu);
     void delStu(int stuId);
     void updateStu(Stu stu);
     Stu getStuById(int stuId);
     List<Stu> getPageStu(int pageNum,int pageSize);
     int getStuNum();
     //导出
    InputStream getInputStream() throws Exception;
    //导入
    boolean batchImport(String fileName, MultipartFile file) throws Exception;

}
