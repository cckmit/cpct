package com.zjtelcom.cpct.service.impl.campaign;

import com.zjtelcom.cpct.dao.campaign.MktCamDisplayColumnRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.DisplayColumnLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MessageMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamDisplayColumnRel;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.DisplayColumn;
import com.zjtelcom.cpct.domain.channel.DisplayColumnLabel;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.Message;
import com.zjtelcom.cpct.dto.channel.LabelDTO;
import com.zjtelcom.cpct.dto.channel.MessageLabelInfo;
import com.zjtelcom.cpct.service.campaign.MktCamDisplayColumnRelService;
import com.zjtelcom.cpct.service.channel.MessageLabelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;
import static com.zjtelcom.cpct.constants.CommonConstant.STATUSCD_EFFECTIVE;

@Service
@Transactional
public class MktCamDisplayColumnRelServiceImpl implements MktCamDisplayColumnRelService {

    @Autowired
    private MktCamDisplayColumnRelMapper mktCamDisplayColumnRelMapper;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private MessageLabelService messageLabelService;
    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private DisplayColumnLabelMapper displayColumnLabelMapper;

    /**
     * 获取展示列实例标签列表
     */
    @Override
    public Map<String,Object> findLabelListByDisplayId(Long mktCampaignId, Long displayId) {
        Map<String,Object> result = new HashMap<>();
        List<MktCamDisplayColumnRel> mktCamDisplayColumnRelList = mktCamDisplayColumnRelMapper.selectLabelByCampaignIdAndDisplayId(mktCampaignId,displayId);
        if(mktCamDisplayColumnRelList.size() > 0) {
            List<LabelDTO> labelList = new ArrayList<>();
            List<String> messageTypes = new ArrayList<>();
            List<MessageLabelInfo> mlInfoList = new ArrayList<>();

            for (MktCamDisplayColumnRel mktCamDisplayColumnRel : mktCamDisplayColumnRelList) {
                Label label = injectionLabelMapper.selectByPrimaryKey(mktCamDisplayColumnRel.getInjectionLabelId());
                if (label == null) {
                    continue;
                }
                LabelDTO labelDTO = new LabelDTO();
                labelDTO.setInjectionLabelId(label.getInjectionLabelId());
                labelDTO.setInjectionLabelName(label.getInjectionLabelName());
                labelDTO.setLabelType(label.getLabelType());
                labelDTO.setLabelCode(label.getInjectionLabelCode());
                labelDTO.setMessageType(Long.valueOf(mktCamDisplayColumnRel.getRemark()));
                labelList.add(labelDTO);
                if (!messageTypes.contains(mktCamDisplayColumnRel.getRemark())) {
                    messageTypes.add(mktCamDisplayColumnRel.getRemark());
                }
            }
            for (int i = 0; i < messageTypes.size(); i++) {
                Message message = messageMapper.selectByPrimaryKey(Long.valueOf(messageTypes.get(i)));
                MessageLabelInfo info = BeanUtil.create(message, new MessageLabelInfo());
                List<LabelDTO> dtoList = new ArrayList<>();
                for (LabelDTO dto : labelList) {
                    if (message.getMessageId().equals(dto.getMessageType())) {
                        dtoList.add(dto);
                    }
                }
                info.setLabelDTOList(dtoList);
                //判断是否选中
                if (dtoList.isEmpty()) {
                    info.setChecked("1");//false
                } else {
                    info.setChecked("0");//true
                }
                mlInfoList.add(info);
            }
            result.put("resultCode", CODE_SUCCESS);
            result.put("resultMsg", mlInfoList);
            result.put("labels", labelList);
        }else {
            DisplayColumn displayColumn = new DisplayColumn();
            displayColumn.setDisplayColumnId(displayId);
            result = messageLabelService.queryLabelListByDisplayId(displayColumn);
        }
        return result;
    }
}
