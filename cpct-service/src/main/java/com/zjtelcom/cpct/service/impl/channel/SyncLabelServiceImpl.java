package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.dto.channel.RecordModel;
import com.zjtelcom.cpct.dto.channel.TagModel;
import com.zjtelcom.cpct.dto.channel.TagValueModel;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.SyncLabelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class SyncLabelServiceImpl extends BaseService implements SyncLabelService {
    public static final String BQ = "BQ";
    public static final Long TAG_ROW_ID = 1000000L;

    public static final String OPERATE_TYPE_NEW = "new";
    public static final String OPERATE_TYPE_UPDATE = "update";
    public static final String OPERATE_TYPE_DELETE = "delete";

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
            switch (record.getOperateType()){
                case OPERATE_TYPE_NEW:
                    result = addLabel(record);
                    break;
                case OPERATE_TYPE_UPDATE:
                    result = updateLabel(record);
                    break;
                case OPERATE_TYPE_DELETE:
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
    private Map<String,Object> addLabel(RecordModel record){
        Map<String,Object> result = new HashMap<>();
        TagModel tagModel = record.getTag();
        List<TagValueModel> valueModelList = new ArrayList<>();
        if (record.getTagValueList()!=null && !record.getTagValueList().isEmpty()){
            valueModelList = record.getTagValueList();
        }
        Label label = new Label();
        label.setTagRowId(tagModel.getTagRowId());
        label.setLabelDataType(tagModel.getSourceTableColumnType());
        label.setInjectionLabelCode(tagModel.getSourceTableColumnName());
        label.setInjectionLabelName(tagModel.getTagName());
        label.setInjectionLabelDesc(tagModel.getTagName());
        //todo 暂无标签类型
        label.setLabelType("1000");
        //todo 暂无值类型
        if (record.getTagValueList()!=null && !record.getTagValueList().isEmpty()){
            label.setLabelValueType("2000");
        }else {
            label.setLabelValueType("1000");
        }
        label.setLabelDataType(ChannelUtil.getDataType(tagModel.getSourceTableColumnType()));
        label.setCreateDate(new Date());
        labelMapper.insert(label);
        if (!valueModelList.isEmpty()){
            syncLabelValue(valueModelList,label.getInjectionLabelId());
        }
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
        TagModel tagModel = record.getTag();
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
        TagModel tagModel = record.getTag();
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


    private Map<String,Object> syncLabelValue(List<TagValueModel> valueModelList,Long labelId) {
        Map<String,Object> result = new HashMap<>();
        List<LabelValue> valueList = new ArrayList<>();
        for (TagValueModel info : valueModelList){
            LabelValue value = BeanUtil.create(info,new LabelValue());
            //todo 逗号分隔的value
            value.setLabelValue("");
            value.setInjectionLabelId(labelId);
            value.setValueDesc(info.getTagValueName());
            value.setValueName(info.getTagValueName());
            value.setCreateDate(new Date());
            value.setStatusCd("1000");
            value.setUpdateDate(new Date());
            valueList.add(value);
        }
        labelValueMapper.insertBatch(valueList);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","标签值同步成功");
        return result;
    }
}
