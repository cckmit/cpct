package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.MktProductAttr;
import com.zjtelcom.cpct.service.channel.ProductService;
import com.zjtelcom.cpct.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/productAttr")
public class ProductAttrController extends BaseController {


    @Autowired
    private ProductService productService;


    /**
     *批量新增产品属性
     * @return
     */
    @PostMapping("addMktProductAttr")
    @CrossOrigin
    public Map<String, Object> addMktProductAttr(@RequestBody HashMap<String,Object> param) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = productService.addMktProductAttr(param);
        }catch (Exception e){
            logger.error("[op:ProductAttrController] fail to addMktProductAttr",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addMktProductAttr");
            return result;
        }
        return result;
    }


    /**
     *修改产品属性
     * @return
     */
    @PostMapping("editMktProductAttr")
    @CrossOrigin
    public Map<String, Object> editMktProductAttr(@RequestBody MktProductAttr mktProductAttr) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = productService.editMktProductAttr(mktProductAttr);
        }catch (Exception e){
            logger.error("[op:ProductAttrController] fail to editMktProductAttr",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editMktProductAttr");
            return result;
        }
        return result;
    }

    /**
     *查询产品属性列表
     * @return
     */
    @PostMapping("listMktProductAttr")
    @CrossOrigin
    public Map<String, Object> listMktProductAttr(@RequestBody MktProductAttr mktProductAttr) {
        Map<String ,Object> result = new HashMap<>();
        try {
            result = productService.listMktProductAttr(mktProductAttr);
        }catch (Exception e){
            logger.error("[op:ProductAttrController] fail to listMktProductAttr",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to listMktProductAttr");
            return result;
        }
        return result;
    }











}
