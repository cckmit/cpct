package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.channel.VerbalAddVO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.channel.VerbalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("verbal")
public class VerbalController extends BaseController {
    @Autowired
    private VerbalService verbalService;


    /**
     * 添加痛痒点话术
     */
    @PostMapping("addVerbal")
    public RespInfo addVerbal(Long userId, @RequestBody VerbalAddVO addVO) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = verbalService.addVerbal(userId,addVO);
        } catch (Exception e) {
            logger.error("[op:VerbalController] fail to addVerbal",e);
            return RespInfo.build(CODE_FAIL,"痛痒点话术添加失败");
        }
        return respInfo;
    }

    /**
     * 根据渠道推送配置id获取痛痒点话术列表
     */
    @GetMapping("getVerbalListByConfId")
    public RespInfo getVerbalListByConfId(Long userId, Long confId) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = verbalService.getVerbalListByConfId(userId,confId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[op:VerbalController] fail to getVerbalListByConfId",e);
            return RespInfo.build(CODE_FAIL,"根据渠道推送配置id获取痛痒点话术列表失败");
        }
        return respInfo;

    }

    /**
     * 获取痛痒点话术详情
     */
    @GetMapping("getVerbalDetail")
    public RespInfo getVerbalDetail(Long userId, Long verbalId) {
        RespInfo respInfo = new RespInfo();
        try {
            respInfo = verbalService.getVerbalDetail(userId,verbalId);
        } catch (Exception e) {
            logger.error("[op:VerbalController] fail to getVerbalDetail",e);
            return RespInfo.build(CODE_FAIL,"获取痛痒点话术详情失败");
        }
        return respInfo;
    }

}
