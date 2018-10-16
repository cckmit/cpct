package cpct.dubbo.service.impl;

import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.enums.Operator;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.UserUtil;
import cpct.dubbo.model.LabModel;
import cpct.dubbo.model.LabValueModel;
import cpct.dubbo.model.RecordModel;
import cpct.dubbo.service.SyncLabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;
import static com.zjtelcom.cpct.constants.CommonConstant.STATUSCD_EFFECTIVE;


/**
 * Description:
 * author: hyf
 * date: 2018/07/17 11:11
 * version: V1.0
 */
@Service
public class SyncLabelServiceImpl  implements SyncLabelService {
    public static final Logger logger = LoggerFactory.getLogger(SyncLabelServiceImpl.class);
    @Autowired
    private InjectionLabelMapper labelMapper;
    @Autowired
    private InjectionLabelValueMapper labelValueMapper;


    /**
     * 标签同步对外接口
     * @param record
     * @return
     */
    @Override
    @Transactional
    public Map<String, Object> syncLabelInfo(RecordModel record) {
        Map<String,Object> result = new HashMap<>();
        try {
            switch (record.getLabel().getLabState()){
                case "3":
                    result = addLabel(record);
                    break;
                case "5":
                    result = deleteLabel(record);
                    break;
            }
        }catch (Exception e){
            logger.error("[op:SyncLabelServiceImpl] fail to syncLabelInfo",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to syncLabelInfo");
            return result;
        }
        return result;
    }


    /**
     * 新增标签
     * @param record
     * @return
     */
    private Map<String,Object> addLabel(RecordModel record) {
        Map<String, Object> result = new HashMap<>();
        LabModel labModel = record.getLabel();
        Label labelValodate = labelMapper.selectByTagRowId(record.getLabel().getLabRowId());
        if (labelValodate != null) {
            labelValueMapper.deleteByLabelId(labelValodate.getInjectionLabelId());
            labelMapper.deleteByPrimaryKey(labelValodate.getInjectionLabelId());
        }
        List<LabValueModel> valueModelList = new ArrayList<>();
        if (record.getLabelValueList() != null && !record.getLabelValueList().isEmpty()) {
            valueModelList = record.getLabelValueList();
        }
        Label label = BeanUtil.create(labModel, new Label());
        label.setTagRowId(labModel.getLabRowId());//标签id
        label.setInjectionLabelName(labModel.getLabName());//标签名称
        label.setInjectionLabelCode(labModel.getLabEngName());//英文名称
        //label.setInjectionLabelDesc();
        label.setLabTagCode(labModel.getLabCode());//标签编码

        label.setConditionType(labModel.getLabType());//标签类型(文本、数值、枚举) 4输入  2多选
        if(Long.valueOf(labModel.getLabType()) == 4){
            label.setOperator("2000,3000,1000,4000,6000,5000,7000,7200");
        }else if(Long.valueOf(labModel.getLabType()) == 2){
            label.setOperator("7000");
        }
        label.setScope(1);
        label.setIsShared(0);
        //label.setCatalogId();

        //todo 暂无标签类型
        label.setLabelType("1000");
        //todo 暂无值类型
        if (record.getLabelValueList()!=null && !record.getLabelValueList().isEmpty()){
            label.setLabelValueType("2000");
        }else {
            label.setLabelValueType("1000");
        }
        //label.setLabelDataType(ChannelUtil.getDataType(tagModel.getSourceTableColumnType()));

        label.setStatusCd(STATUSCD_EFFECTIVE);
        label.setCreateStaff(UserUtil.loginId());
        label.setCreateDate(new Date());

        labelMapper.insert(label);
        syncLabelValue(valueModelList,label.getInjectionLabelId());
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","新增成功");
        return result;
    }

    /**
     * 更新标签
     * @param record
     * @return
     */
    private Map<String,Object> updateLabel(RecordModel record){
        Map<String,Object> result = new HashMap<>();
        //todo 先删除再添加
        LabModel tagModel = record.getLabel();
        Label label = labelMapper.selectByTagRowId(tagModel.getLabRowId());
//        if (label==null){
//            result.put("resultCode",CODE_FAIL);
//            result.put("resultMsg","未找到对应标签");
//            return result;
//        }
        labelValueMapper.deleteByLabelId(label.getInjectionLabelId());
        labelMapper.deleteByPrimaryKey(label.getInjectionLabelId());
        addLabel(record);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","更新成功");
        return result;
    }

    /**
     * 删除标签
     * @param record
     * @return
     */
    private Map<String,Object> deleteLabel(RecordModel record){
        Map<String,Object> result = new HashMap<>();
        LabModel tagModel = record.getLabel();
        Label label = labelMapper.selectByTagRowId(tagModel.getLabRowId());
        if (label==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","未找到对应标签");
            return result;
        }
//        List<LabelValue> valueList = labelValueMapper.selectByLabelId(label.getInjectionLabelId());
        labelValueMapper.deleteByLabelId(label.getInjectionLabelId());
        labelMapper.deleteByPrimaryKey(label.getInjectionLabelId());
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");
        return result;
    }


    private Map<String,Object> syncLabelValue(List<LabValueModel> valueModelList, Long labelId) {
        Map<String,Object> result = new HashMap<>();
        List<LabelValue> valueList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        for (LabValueModel info : valueModelList){
            LabelValue value = BeanUtil.create(info,new LabelValue());
            value.setInjectionLabelId(labelId);
            value.setValueDesc(info.getValueName());
            value.setValueName(info.getValueName());
            value.setLabelValue(info.getLabValue());
            value.setCreateDate(new Date());
            value.setStatusCd("1000");
            value.setUpdateDate(new Date());
            valueList.add(value);
            stringList.add(value.getLabelValue());
            labelValueMapper.insert(value);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","标签值同步成功");
        result.put("valueString",ChannelUtil.StringList2String(stringList));
        return result;
    }
}
