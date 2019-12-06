package com.hlk.service.impl;

import com.github.pagehelper.PageHelper;

import com.hlk.common.MyException;
import com.hlk.dao.StuMapper;
import com.hlk.poi.WriteExcel;
import com.hlk.pojo.Stu;
import com.hlk.service.StuService;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpSession;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2017/4/25.
 */
@Transactional(propagation= Propagation.REQUIRED,isolation= Isolation.DEFAULT,timeout=5)
@Service("stuService")
public class StuServiceImpl implements StuService {

    @Autowired
    private StuMapper stuMapper;


    public Stu getStuById(int stuId) {
        Stu stu = stuMapper.getStuById(stuId);
        return stu;
    }

    public void addStu(Stu stu) {
        /*HttpSession session = getSession();
        String username = (String)session.getAttribute("username");
        User user  = userMapper.getUserByUserName(username);
        stu.setUser(user);*/
        stuMapper.addStu(stu);
    }

    public void delStu(int stuId) {
        stuMapper.delStu(stuId);
    }

    public void updateStu(Stu stu) {
        stuMapper.updateStu(stu);
    }

    public int getStuNum() {
        List<Stu> list = stuMapper.getStuList();
        return list.size();
    }

    public List<Stu> getPageStu(int pagenum, int pagesize) {
        PageHelper.startPage(pagenum,pagesize);//分页核心代码
        List<Stu> list = stuMapper.getStuList();
        return list;
    }

    public InputStream getInputStream() throws Exception {
        String[] title=new String[]{"stuId","stuName","stuAge","stuMajor"};
        List<Stu> plist = stuMapper.getStuList();
        List<Object[]>  dataList = new ArrayList<Object[]>();
        for(int i = 0; i< plist.size(); i++){
            Object[] obj=new Object[4];
            obj[0]= plist.get(i).getStuId();
            obj[1]= plist.get(i).getStuName();
            obj[2]= plist.get(i).getStuAge();
            obj[3]= plist.get(i).getStuMajor();
            dataList.add(obj);
        }
        WriteExcel ex = new WriteExcel(title, dataList);
        InputStream in;
        in = ex.export();
        return in;
    }

    /*public static HttpSession getSession() {
        HttpSession session = null;
        try {
            session = getRequest().getSession();
        } catch (Exception e) {}
        return session;
    }*/

    /*public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs =(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }*/


    public boolean batchImport(String fileName, MultipartFile file) throws Exception {
        boolean notNull = false;
        List<Stu> stuList =  new ArrayList<Stu>();
        InputStream is = file.getInputStream();
        Workbook wb = null;
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            throw new MyException("上传文件格式不正确");
        }
        boolean isExcel2003 = true;
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }
        if (isExcel2003) {
            wb = new HSSFWorkbook(is);
        } else {
            wb = new XSSFWorkbook(is);
        }
        Sheet sheet = wb.getSheetAt(0);
        if (sheet!=null){
            notNull = true;
        }
        Stu stu;
        boolean flag = false;
        CellReference cellReference = new CellReference("A4");
        for (int i = cellReference.getRow();i<= sheet.getLastRowNum();){
            Row r = sheet.getRow(i);
            if (r==null){
                //如果是空行（即没有任何数据、格式），直接把它以下的数据往上移动
                sheet.shiftRows(i+1, sheet.getLastRowNum(),-1);
                continue;
            }
            flag = false;
            for(Cell c : r){
                if(c.getCellType() != Cell.CELL_TYPE_BLANK){
                    flag = true;
                    break;
                }
            }
            if(flag){
                i++;
                continue;
            }else{//如果是空白行（即可能没有数据，但是有一定格式）
                if(i == sheet.getLastRowNum()){
                    //如果到了最后一行，直接将那一行remove掉
                    sheet.removeRow(r);
                }else{
                    //如果还没到最后一行，则数据往上移一行
                    sheet.shiftRows(i+1, sheet.getLastRowNum(),-1);
                }
            }
        }
        stu = new Stu();
        for (int r = 2; r <= sheet.getLastRowNum(); r++) {//r = 2 表示从第三行开始循环 如果你的第三行开始是数据
            Row row = sheet.getRow(r);//通过sheet表单对象得到 行对象
            if (row ==null){
                continue;
            }
            stu = new Stu();

            if (row.getCell(0).getCellType()!=1){//循环时，得到每一行的单元格进行判断
                throw new MyException("导入失败，StuName");
            }

            String stuName = row.getCell(0).getStringCellValue();
            if (stuName == null ){
                throw new MyException("stuName为空");
            }

            int stuAge = (int) row.getCell(1).getNumericCellValue();

            String stuMajor = row.getCell(2).getStringCellValue();
            if (stuMajor == null ){
                throw new MyException("stuMajor为空");
            }

            stu.setStuName(stuName);
            stu.setStuAge(stuAge);
            stu.setStuMajor(stuMajor);
            stuList.add(stu);
        }
        for (Stu stuResord : stuList){
           stuMapper.addStu(stuResord);
        }
        return notNull;
    }


}
