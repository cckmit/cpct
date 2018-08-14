package com.zjtelcom.cpct.service.impl.channel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.request.channel.DisplayAllMessageReq;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.MessageLabelService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 信息关联标签实现类
 */
@Service
public class MessageLabelServiceImpl extends BaseService implements MessageLabelService {

    @Autowired
    private MessageLabelMapper messageLabelMapper;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private DisplayColumnMapper displayColumnMapper;
    @Autowired
    private DisplayColumnLabelMapper displayColumnLabelMapper;

    /**
     * 查询出信息关联标签列表
     */
    @Override
    public Map<String, Object> qureyMessageLabel(Message message) {
        Map<String, Object> maps = new HashMap<>();
        MessageLabelDTO messageLabelDTO = new MessageLabelDTO();
        List<LabelDTO> labelDTOList = new ArrayList<>();
        List<MessageLabel> messageLabelList = messageLabelMapper.qureyMessageLabel(message);
        messageLabelDTO.setMessageId(message.getMessageId());
        for (MessageLabel messageLabel : messageLabelList) {
            LabelDTO labelDTO = new LabelDTO();
            labelDTO.setInjectionLabelId(messageLabel.getInjectionLabelId());
            Label label = injectionLabelMapper.selectByPrimaryKey(messageLabel.getInjectionLabelId());
            labelDTO.setInjectionLabelName(label.getInjectionLabelName());
            labelDTOList.add(labelDTO);
        }
        messageLabelDTO.setMessageLabelId(labelDTOList);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("messageLabelDTO", messageLabelDTO);
        return maps;
    }

    /**
     * 查询出所有信息
     */
    @Override
    public Map<String, Object> queryMessages() {
        Map<String, Object> maps = new HashMap<>();
        List<MessageVO> messageVOList = new ArrayList<>();
        List<Message> messageList = messageMapper.selectAll();
        for (Message message : messageList) {
            MessageVO messageVO = new MessageVO();
            messageVO.setMessageId(message.getMessageId());
            messageVO.setMessageName(message.getMessageName());
            messageVOList.add(messageVO);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("messageVOList", messageVOList);
        return maps;
    }

    /**
     * 新增标签组
     */
    @Override
    public Map<String, Object> createLabelGroup(DisplayColumn displayColumn) {
        Map<String, Object> maps = new HashMap<>();
        displayColumn.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
        displayColumn.setCreateStaff(UserUtil.loginId());
        displayColumn.setUpdateStaff(UserUtil.loginId());
        displayColumn.setCreateDate(DateUtil.getCurrentTime());
        displayColumn.setUpdateDate(DateUtil.getCurrentTime());
        displayColumn.setStatusDate(DateUtil.getCurrentTime());
        displayColumnMapper.insert(displayColumn);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 查询出所有展示列
     */
    @Override
    public Map<String, Object> queryDisplays() {
        Map<String, Object> maps = new HashMap<>();
        List<DisplayColumnVO> displayColumnVOList = new ArrayList<>();
        List<DisplayColumn> displayColumnList = displayColumnMapper.selectAll();
        for (DisplayColumn displayColumn : displayColumnList) {
            DisplayColumnVO displayColumnVO = new DisplayColumnVO();
            displayColumnVO.setDisplayColumnId(displayColumn.getDisplayColumnId());
            displayColumnVO.setDisplayColumnName(displayColumn.getDisplayColumnName());
            displayColumnVOList.add(displayColumnVO);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("displayColumnVOList", displayColumnVOList);
        return maps;
    }

    /**
     * 保存展示列所有信息
     */
    @Override
    public Map<String, Object> createDisplayAllMessage(DisplayAllMessageReq displayAllMessageReq) {
        Map<String, Object> maps = new HashMap<>();
        List<Long> injectionLabelIds = displayAllMessageReq.getInjectionLabelIds();
        for (Long injectionLabelId : injectionLabelIds) {
            DisplayColumnLabel displayColumnLabel = new DisplayColumnLabel();
            displayColumnLabel.setInjectionLabelId(injectionLabelId);
            displayColumnLabel.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            displayColumnLabel.setCreateStaff(UserUtil.loginId());
            displayColumnLabel.setUpdateStaff(UserUtil.loginId());
            displayColumnLabel.setCreateDate(DateUtil.getCurrentTime());
            displayColumnLabel.setUpdateDate(DateUtil.getCurrentTime());
            displayColumnLabel.setStatusDate(DateUtil.getCurrentTime());
            displayColumnLabelMapper.insert(displayColumnLabel);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 编辑展示列
     */
    @Override
    public Map<String, Object> viewDisplayColumn(DisplayAllMessageReq displayAllMessageReq) {
        Map<String, Object> map = new HashMap<>();
        Page page = displayAllMessageReq.getPage();
        PageHelper.startPage(page.getPage(), page.getPageSize());
        List<Long> injectionLabelIds = displayAllMessageReq.getInjectionLabelIds();
        List<Label> labelList = injectionLabelMapper.queryLabelsExceptSelected(injectionLabelIds);
        List<LabelDTO> labelDTOList = new ArrayList<>();
        for (Label label : labelList) {
            LabelDTO labelDTO = new LabelDTO();
            labelDTO.setInjectionLabelId(label.getInjectionLabelId());
            labelDTO.setInjectionLabelName(label.getInjectionLabelName());
            labelDTOList.add(labelDTO);
        }
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("labelList", labelDTOList);
        map.put("pageInfo", new Page(new PageInfo(labelDTOList)));
        return map;
    }


}
