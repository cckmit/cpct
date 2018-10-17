package com.zjtelcom.cpct.dubbo.controller;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dubbo.model.RecordModel;
import com.zjtelcom.cpct.dubbo.service.SyncLabelService;
import com.zjtelcom.cpct.dubbo.model.LabModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/label")
public class SyncLabelController {
    @Autowired
    private SyncLabelService syncLabelService;

    @RequestMapping(value = "syncLabel", method = RequestMethod.POST)
    @CrossOrigin
    public String syncLabel() {
        Map result = new HashMap();
//        try{
//            InputStream inputStream = multipartFile.getInputStream();
//            XSSFWorkbook wb = new XSSFWorkbook(inputStream);
//            Sheet sheet = wb.getSheetAt(0);
//            Integer rowNums = sheet.getLastRowNum() + 1;
//            for (int i = 1; i < rowNums - 1; i++) {
//                Row rowFirst = sheet.getRow(0);
//                Row row = sheet.getRow(i);
//                Cell cell = row.getCell(0);
//            }
//        }catch (Exception e) {
//            e.printStackTrace();
//        }

        //result = syncLabelService.initialization();
        result = syncLabelService.listLabelCatalog();
        return JSON.toJSON(result).toString();
    }




}
