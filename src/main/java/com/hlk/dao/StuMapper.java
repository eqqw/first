package com.hlk.dao;

import com.hlk.pojo.Stu;
import java.util.List;

/**
 * Created by lenovo on 2017/4/25.
 */
public interface StuMapper {
     void addStu(Stu stu);
     void delStu(int stuId);
     void updateStu(Stu stu);
     Stu getStuById(int stuId);
     List<Stu> getStuList();
}
