package cpct.dubbo.service.impl;

import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.enums.Operator;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.UserUtil;
import cpct.dubbo.model.LabelModel;
import cpct.dubbo.model.LabelValueModel;
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
            switch (record.getTag().getLabState()){
                case "3":
                    result = addLabel(record);
                    break;
                case "5":
                    result = updateLabel(record);
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
    private Map<String,Object> addLabel(RecordModel record){
        Map<String,Object> result = new HashMap<>();
        LabelModel tagModel = record.getTag();
        Label labelValodate = labelMapper.selectByTagRowId(record.getTag().getTagRowId());
        if (labelValodate!=null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","标签已存在，请选择更新或删除后添加");
            return result;
        }
        List<LabelValueModel> valueModelList = new ArrayList<>();
        if (record.getTagValueList()!=null && !record.getTagValueList().isEmpty()){
            valueModelList = record.getTagValueList();
        }
        Label label = new Label();
        label.setTagRowId(tagModel.getTagRowId());
        //todo
        label.setLabelDataType("");
        label.setInjectionLabelCode(tagModel.getSourceTableColumnName());
        label.setInjectionLabelName(tagModel.getTagName());
        label.setInjectionLabelDesc(tagModel.getTagName());
        label.setStatusCd(STATUSCD_EFFECTIVE);
        label.setCreateStaff(UserUtil.loginId());
        label.setCreateDate(new Date());
        //todo
        label.setConditionType("");
        //todo 标签操作符 对应显示类型
        label.setOperator(String.valueOf(Operator.EQUAL.getValue()));
        label.setScope(1);
        label.setIsShared(0);
        //todo 暂无标签类型
        label.setLabelType("1000");
        //todo 暂无值类型
        if (record.getTagValueList()!=null && !record.getTagValueList().isEmpty()){
            label.setLabelValueType("2000");
        }else {
            label.setLabelValueType("1000");
        }
//        label.setLabelDataType(ChannelUtil.getDataType(tagModel.getSourceTableColumnType()));
        label.setCreateDate(new Date());
        labelMapper.insert(label);
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
        LabelModel tagModel = record.getTag();
        Label label = labelMapper.selectByTagRowId(tagModel.getTagRowId());
        if (label==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","未找到对应标签");
            return result;
        }
        List<LabelValue> valueList = labelValueMapper.selectByLabelId(label.getInjectionLabelId());
        if (!valueList.isEmpty()){
            for (LabelValue value : valueList){
                labelValueMapper.deleteByPrimaryKey(value.getLabelValueId());
            }
        }
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
        LabelModel tagModel = record.getTag();
        Label label = labelMapper.selectByTagRowId(tagModel.getTagRowId());
        if (label==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","未找到对应标签");
            return result;
        }
        List<LabelValue> valueList = labelValueMapper.selectByLabelId(label.getInjectionLabelId());
        if (!valueList.isEmpty()){
            for (LabelValue value : valueList){
                labelValueMapper.deleteByPrimaryKey(value.getLabelValueId());
            }
        }
        labelMapper.deleteByPrimaryKey(label.getInjectionLabelId());
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");
        return result;
    }


    private Map<String,Object> syncLabelValue(List<LabelValueModel> valueModelList,Long labelId) {
        Map<String,Object> result = new HashMap<>();
        List<LabelValue> valueList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        for (LabelValueModel info : valueModelList){
            LabelValue value = BeanUtil.create(info,new LabelValue());
            value.setInjectionLabelId(labelId);
            value.setValueDesc(info.getTagValueName());
            value.setValueName(info.getTagValueName());
            value.setLabelValue(info.getTagDownValue());
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
