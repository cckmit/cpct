package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.PpmProduct;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.channel.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@RestController
@RequestMapping("/ppmProduct")
public class PpmProductController extends BaseController  {

    @Autowired
    private ProductService productService;

    /**
     * 获取销售品列表
     */
    @GetMapping("getProductList")
    public Map<String,Object> getProductList(Long userId, String productName){
        Map<String ,Object> result = new HashMap<>();
        List<PpmProduct> productList = new ArrayList<>();
        try {
            productList = productService.getProductList(userId,productName);
        }catch (Exception e){
            logger.error("[op:PpmProductController] fail to getProductList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to addCamScript");
            return result;
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",productList);
        return result;
    }

}
