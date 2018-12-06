package com.zjtelcom.cpct.dubbo.controller;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dubbo.model.RecordModel;
import com.zjtelcom.cpct.dubbo.service.SyncEventService;
import com.zjtelcom.cpct.dubbo.service.SyncLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/label")
public class SyncLabelController {
    @Autowired
    private SyncLabelService syncLabelService;

    @Autowired
    private SyncEventService syncEventService;

    @RequestMapping(value = "syncLabel", method = RequestMethod.POST)
    @CrossOrigin
    public String syncLabel(@RequestBody RecordModel model) {
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

        result = syncLabelService.initialization();
//        result = syncLabelService.syncLabelInfo(model);
        return JSON.toJSON(result).toString();
    }

    @RequestMapping(value = "syncEvent", method = RequestMethod.POST)
    @CrossOrigin
    public String syncEvent(@RequestBody Map<String,Object> param) {
        syncEventService.syncEvent(param);
        return "调用成功";
    }



}
