package com.zjtelcom.cpct.controller.campaign;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.campaign.MktCamDisplayColumnRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/mktCamDisplayColumnRel")
public class MktCamDisplayColumnRelController extends BaseController {

    @Autowired
    private MktCamDisplayColumnRelService mktCamDisplayColumnRelService;

    /**
     * 获取展示列实例标签列表
     */
    @PostMapping("/findLabelListByDisplayId")
    @CrossOrigin
    public Map<String, Object> findLabelListByDisplayId(@RequestBody HashMap<String, String> param) {
        Map<String, Object> result = new HashMap<>();
        Long mktCampaignId = 0L;
        Long displayId = 0L;
        if (param.get("mktCampaignId") != null) {
            mktCampaignId = Long.valueOf(param.get("mktCampaignId"));
        }
        if (param.get("displayColumnId") != null) {
            displayId = Long.valueOf(param.get("displayColumnId"));
        }
        result = mktCamDisplayColumnRelService.findLabelListByDisplayId(mktCampaignId, displayId);
        return result;
    }

    /**
     * 展示列实例化复制
     *
     *  查询老活动下的所有展示列标签，复制插入到新活动下
     * @param param
     * @return
     */
    @PostMapping("/copyDisplayLabelByCamId")
    @CrossOrigin
    public Map<String, Object> copyDisplayLabelByCamId(@RequestBody HashMap<String, String> param) {
        Map<String, Object> result = new HashMap<>();
        Long oldCampaignId = 0L;
        Long newCampaignId = 0L;
        oldCampaignId = Long.valueOf(param.get("oldCampaignId"));
        newCampaignId = Long.valueOf(param.get("newCampaignId"));
        mktCamDisplayColumnRelService.copyDisplayLabelByCamId(oldCampaignId, newCampaignId);
        return result;
    }

    @PostMapping("/importOldCamDisplay2")
    public void importOldCamDisplay(){
        try {
            mktCamDisplayColumnRelService.importOldCamDisplay();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
