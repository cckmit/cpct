package com.zjtelcom.cpct.dubbo.out.impl;


import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dubbo.out.TargetGroupService;
import com.zjtelcom.cpct.service.grouping.TarGrpTemplateService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.enums.DateUnit.DAY;

@Service
public class TargetGroupServiceImpl implements TargetGroupService {

    @Autowired(required = false)
    private TarGrpTemplateService tarGrpTemplateService;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;
    @Autowired
    private TarGrpMapper tarGrpMapper;
    @Autowired
    private RedisUtils redisUtils;


    /**
     * 分群定时批量下发
     */
    @Override
    public Map<String, Object> tarGrpTemplateScheduledBatchIssue() {
        return tarGrpTemplateService.tarGrpTemplateScheduledBatchIssue();
    }

    List<String> labelIdList;

    /**
     * 更新缓存中分群的自定义日期类型标签的值（每日）
     */
    @Override
    public void updateRedisOfDateTypeLabel() {

        labelIdList = injectionLabelMapper.selectLabelIdByType("1100");
        for (String id : labelIdList) {
            List<TarGrpCondition> tarGrpConditionList = tarGrpConditionMapper.findListBylabelId(Long.parseLong(id));
            if (tarGrpConditionList == null)
                continue;
            for (TarGrpCondition tarGrpCondition : tarGrpConditionList) {
                if (!"1".equals(tarGrpCondition.getUpdateStaff()))
                    continue;
                Long tarGrpId = tarGrpCondition.getTarGrpId();
                List<TarGrpCondition> conditionList = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
                String resultString = generateExpress(conditionList);
                redisUtils.del("EXPRESS_" + tarGrpId);
                redisUtils.set("EXPRESS_" + tarGrpId, resultString);
            }
        }
    }

    public String generateExpress(List<TarGrpCondition> conditionList) {
        StringBuilder express = new StringBuilder();
        express.append("if(");
        for (TarGrpCondition tarGrpCondition : conditionList) {
            String labelId = tarGrpCondition.getLeftParam();
            String type = tarGrpCondition.getOperType();
            String rightParam = tarGrpCondition.getRightParam();
            Long updateStaff = tarGrpCondition.getUpdateStaff();
            Label code = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(labelId));
            if ("PROM_LIST".equals(code)) {
                express.append("(checkProm(").append(code).append(",").append(type).append(",").append(rightParam);
                express.append("))");
            } else if (labelIdList.contains(labelId)) {
                // todo 时间类型标签
                if (1L == updateStaff) {
                    rightParam = DateUtil.addDate(new Date(), Integer.parseInt(rightParam), DAY).toString();
                }
                express.append("(dateLabel(").append(code).append(",").append(type).append(",").append("\"" + rightParam + "\"");
                express.append("))");
            } else {
                if ("7100".equals(type)) {
                    express.append("!");
                }
                express.append("((");
                express.append(assLabel(code.toString(), type, rightParam));
                express.append(")");
            }
            express.append("&&");
        }
        express.delete(express.length() - 2, express.length());
        express.append(") {return true} else {return false}");
        return JSON.toJSONString(express);
    }

    public static String assLabel(String code, String type, String rightParam) {
        StringBuilder express = new StringBuilder();
        switch (type) {
            case "1000":
                express.append("toNum(").append(code).append("))");
                express.append(" > ");
                express.append(rightParam);
                break;
            case "2000":
                express.append("toNum(").append(code).append("))");
                express.append(" < ");
                express.append(rightParam);
                break;
            case "3000":
                express.append("toNum(").append(code).append("))");
                express.append(" == ");
                if (NumberUtils.isNumber(rightParam)) {
                    express.append(rightParam);
                } else {
                    express.append("\"").append(rightParam).append("\"");
                }
                break;
            case "4000":
                express.append("toNum(").append(code).append("))");
                express.append(" != ");
                if (NumberUtils.isNumber(rightParam)) {
                    express.append(rightParam);
                } else {
                    express.append("\"").append(rightParam).append("\"");
                }
                break;
            case "5000":
                express.append("toNum(").append(code).append("))");
                express.append(" >= ");
                express.append(rightParam);
                break;
            case "6000":
                express.append("toNum(").append(code).append("))");
                express.append(" <= ");
                express.append(rightParam);
                break;
            case "7100":    //不包含于
            case "7000":    //包含于
                express.append(code).append(")");
                express.append(" in ");
                String[] strArray = rightParam.split(",");
                express.append("(");
                for (int j = 0; j < strArray.length; j++) {
                    express.append("\"").append(strArray[j]).append("\"");
                    if (j != strArray.length - 1) {
                        express.append(",");
                    }
                }
                express.append(")");
                break;
            case "7200":  //区间于
                express.append("toNum(").append(code).append("))");
                String[] strArray2 = rightParam.split(",");
                express.append(" >= ").append(strArray2[0]);
                express.append(" && ").append("(toNum(");
                express.append(code).append("))");
                express.append(" <= ").append(strArray2[1]);
                break;
        }
        return express.toString();
    }
}
