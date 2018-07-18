package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.zjhcsoft.eagle.main.dubbo.model.policy.*;
import com.zjhcsoft.eagle.main.dubbo.service.TagSyncService;
import com.zjtelcom.cpct.common.CacheConstants;
import com.zjtelcom.cpct.common.CacheManager;
import com.zjtelcom.cpct.common.IDacher;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.system.SystemParam;
import com.zjtelcom.cpct.service.channel.SyncLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;

import java.util.ArrayList;
import java.util.List;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@RestController
@RequestMapping("syncLabel")
public class SyncLabelController extends BaseController {

    @Autowired
    private SyncLabelService syncLabelService;

//    @Autowired(required = false)
//    private SyncEagleDataService syncEagleDataService;

    @SuppressWarnings("unchecked")
    public int getSyncTimes() {

        logger.debug("CacheManager.getInstance(): " + CacheManager.getInstance());
        IDacher<SystemParam> cache = CacheManager.getInstance().getCache(
                CacheConstants.SYSTEMPARAM_CACHE_NAME);

        logger.debug("CacheManager.getInstance().getCache: " + cache);
        if (null == cache) {
            return 50;
        }
        SystemParam param = cache.queryOne("cpc.sync.eagle.count");
        logger.debug("param: " + param);
        int times = Integer.valueOf(param.getParamValue());
        return times;
    }

    public void execute() {
        try {
            // 先查询个数

            TagSyncService tagSyncService = ContextLoader.getCurrentWebApplicationContext().getBean(
                    TagSyncService.class);
            CountResponseHeaderModel countResp = tagSyncService.getTagCount();

            // 非0代表处理失败了
            if (!CODE_SUCCESS.equals(countResp.getResponseHeaderModel().getResultCode())) {
                logger.error("getTagCount error, resp: " + JSON.toJSONString(countResp));
                return;
            }

            logger.debug("getTagCount resp: " + JSON.toJSONString(countResp));

            int count = countResp.getCount();

            int times = getSyncTimes();
            List<TagInfoModel> tagList = new ArrayList<>();
            for (int i = 0; i < count; i = i + times) {
                int endNum = 0;
                // 最后一次同步
                if (count - i <= times) {
                    endNum = count;
                }
                else {
                    endNum = i + times;
                }

                List<TagInfoModel> tagInfo = getTagInfo(tagSyncService, i, endNum);
                tagList.addAll(tagInfo);
            }

//            syncLabelService.syncLabel(tagList);
            //同步完成之后再同步值
            executeLabelValue();
        }
        catch (Exception e) {
            logger.error("SyncTagJob error", e);
        }
    }

    private List<TagInfoModel> getTagInfo(TagSyncService tagSyncService, int startNum, int endNum) {
        List<TagInfoModel> tagInfo = new ArrayList<>();
        RecordModel recordModel = new RecordModel();
        recordModel.setStartNum(startNum);
        recordModel.setEndNum(endNum);
        TagResponseModel tagResp = tagSyncService.syncTag(recordModel);

        if (!CODE_SUCCESS.equals(tagResp.getHeader().getResultCode())) {
            logger.error("syncTag error, resp: " + JSON.toJSONString(tagResp));
        }
        else {
            logger.debug("syncTag resp: " + JSON.toJSONString(tagResp));
            tagInfo = tagResp.getBody().getTagList();
        }
        return tagInfo;
    }





//******************************************************************************************

    public void executeLabelValue() {
        try {
            // 先查询个数
            TagSyncService tagSyncService = ContextLoader.getCurrentWebApplicationContext().getBean(
                    TagSyncService.class);
            CountResponseHeaderModel countResp = tagSyncService.getTagValueCount();

            // 非0代表处理失败了
            if (!CODE_SUCCESS.equals(countResp.getResponseHeaderModel().getResultCode())) {
                logger.error("getTagValueCount error, resp: " + JSON.toJSONString(countResp));
                return;
            }
            logger.debug("getTagValueCount resp: " + JSON.toJSONString(countResp));

            int count = countResp.getCount();

            int times = getSyncTimes();
            List<TagValueInfoModel> dataList = new ArrayList<>();
            for (int i = 0; i < count; i = i + times) {
                int endNum = 0;
                // 最后一次同步
                if (count - i <= times) {
                    endNum = count;
                }
                else {
                    endNum = i + times;
                }

                List<TagValueInfoModel> tagValueList = getTagValueInfo(tagSyncService, i, endNum);
                dataList.addAll(tagValueList);
            }
//            syncLabelService.syncLabelValue(dataList);
        }
        catch (Exception e) {
            logger.error("syncTagValue error", e);
        }
    }

    private List<TagValueInfoModel> getTagValueInfo(TagSyncService tagSyncService, int startNum,
                                                    int endNum) {
        List<TagValueInfoModel> tagValueList = new ArrayList<>();
        RecordModel recordModel = new RecordModel();
        recordModel.setStartNum(startNum);
        recordModel.setEndNum(endNum);
        TagValueResponseModel tagValueResp = tagSyncService.syncTagValue(recordModel);

        if (!CODE_SUCCESS.equals(tagValueResp.getHeader().getResultCode())) {
            logger.error("syncTagValue error, resp: " + JSON.toJSONString(tagValueResp));
        }
        else {
            logger.debug("syncTag resp: " + JSON.toJSONString(tagValueResp));
            tagValueList = tagValueResp.getBody().getTagValueList();
        }

        return tagValueList;
    }

}
