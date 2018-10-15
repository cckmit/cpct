package cpct.dubbo.controller;

import com.alibaba.fastjson.JSON;
import cpct.dubbo.service.SyncLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/syncLabel")
public class SyncLabelController {
    @Autowired
    private SyncLabelService syncLabelService;


    @RequestMapping(value = "syncLabel", method = RequestMethod.POST)
    @CrossOrigin
    public String listStaff(@RequestBody Map<String,String> params) {
        Map result = new HashMap();

        return JSON.toJSON(result).toString();
    }




}
