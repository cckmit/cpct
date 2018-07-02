package com.zjtelcom.cpct.service.impl.channel;

import com.zjhcsoft.eagle.main.dubbo.model.policy.TagInfoModel;
import com.zjhcsoft.eagle.main.dubbo.model.policy.TagValueInfoModel;
import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.SyncLabelService;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class SyncLabelServiceImpl extends BaseService implements SyncLabelService {
    public static final String BQ = "BQ";
    public static final Long TAG_ROW_ID = 1000000L;

    @Autowired
    private InjectionLabelMapper labelMapper;
    @Autowired
    private InjectionLabelValueMapper labelValueMapper;

    @Override
    public RespInfo syncLabel(List<TagInfoModel> tagInfoModelList) {
        List<Label> labelList = new ArrayList<>();

        for (TagInfoModel info : tagInfoModelList){
            Label label = new Label();
            label.setInjectionLabelId(info.getTagRowId()+TAG_ROW_ID);
            label.setLabelDataType(info.getSourceTableColumnType());
            label.setInjectionLabelCode(BQ+ChannelUtil.getRandomStr(8));
            label.setInjectionLabelName(info.getSourceTableColumnName());
            label.setInjectionLabelDesc(info.getTagName());
            //todo 暂无标签类型
            label.setLabelType("4000");
            //todo 暂无值类型
            label.setLabelValueType("2000");
            label.setLabelDataType(ChannelUtil.getDataType(info.getSourceTableColumnType()));
            label.setCreateDate(new Date());
            labelList.add(label);
        }
        labelMapper.deleteAll();
        labelMapper.insertBatch(labelList);
        return RespInfo.build(CODE_SUCCESS,null);
    }


    @Override
    public RespInfo syncLabelValue(List<TagValueInfoModel> tagValueInfoModelList) {
        List<LabelValue> valueList = new ArrayList<>();

        for (TagValueInfoModel info : tagValueInfoModelList){
            LabelValue value = new LabelValue();
            value.setInjectionLabelId(info.getTagRowId()+TAG_ROW_ID);
            value.setLabelValue("");
            valueList.add(value);
        }
        labelValueMapper.deleteAll();
        labelValueMapper.insertBatch(valueList);
        return RespInfo.build(CODE_SUCCESS,null);
    }
}
