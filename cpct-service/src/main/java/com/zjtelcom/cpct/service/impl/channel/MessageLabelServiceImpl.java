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
import com.zjtelcom.cpct.request.channel.MessageReq;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.MessageLabelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

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
     * 查询标签列表
     * @param labelIdList
     * @return
     */
    @Override
    public Map<String, Object> qureyLabelListByIdList(ProductParam labelIdList) {
        Map<String, Object> maps = new HashMap<>();
        List<Label> labelList = new ArrayList<>();
        for (Long id : labelIdList.getIdList()){
            Label label = injectionLabelMapper.selectByPrimaryKey(id);
            if (label==null){
                continue;
            }
            labelList.add(label);
        }
        maps.put("resultCode", CODE_SUCCESS);
        maps.put("resultMsg", labelList);
        return maps;
    }

    /**
     * 获取展示列标签列表
     * @param req
     * @return
     */
    @Override
    public Map<String, Object> queryLabelListByDisplayId(DisplayColumn req) {
        Map<String, Object> maps = new HashMap<>();
        List<DisplayColumnLabel> realList = displayColumnLabelMapper.findListByDisplayId(req.getDisplayColumnId());
        List<Label> labelList = new ArrayList<>();
        for (DisplayColumnLabel real : realList){
            Label label = injectionLabelMapper.selectByPrimaryKey(real.getInjectionLabelId());
            if (label==null){
                continue;
            }
            labelList.add(label);
        }
        List<LabelDTO> labelDTOList = new ArrayList<>();
        for (Label label : labelList) {
            LabelDTO labelDTO = BeanUtil.create(label,new LabelDTO());
            labelDTOList.add(labelDTO);
        }
        maps.put("resultCode", CODE_SUCCESS);
        maps.put("resultMsg", labelDTOList);
        return maps;

    }

    /**
     * 删除展示列
     * @param req
     * @return
     */
    @Override
    public Map<String, Object> delDisplayColumn(DisplayAllMessageReq req) {
        Map<String, Object> maps = new HashMap<>();
        DisplayColumn displayColumn = displayColumnMapper.selectByPrimaryKey(req.getDisplayColumnId());
        if (displayColumn==null){
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", "展示列不存在");
            return maps;
        }
        displayColumnMapper.deleteByPrimaryKey(displayColumn.getDisplayColumnId());
        maps.put("resultCode", CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 删除展示列标签关联
     * @param
     * @return
     */
    @Override
    public Map<String, Object> delColumnLabelRel(Long displayId, Long labelId) {
        Map<String, Object> maps = new HashMap<>();
        DisplayColumnLabel displayColumnLabel = displayColumnLabelMapper.findByDisplayIdAndLabelId(displayId,labelId);
        if (displayColumnLabel!=null){
            displayColumnLabelMapper.deleteByPrimaryKey(displayColumnLabel.getDisplayColumnLabelId());
        }
        maps.put("resultCode", CODE_SUCCESS);
        maps.put("resultMsg", "删除成功");
        return maps;
    }

    @Override
    public Map<String, Object> qureyMessageLabelByMessageIdList(MessageReq req) {
        Map<String, Object> maps = new HashMap<>();
       List<MessageLabelDTO> dtoList = new ArrayList<>();
        for (Long messageId : req.getMessageId()){
            List<LabelDTO> labelDTOList = new ArrayList<>();
            MessageLabelDTO messageLabelDTO = new MessageLabelDTO();
            List<MessageLabel> messageLabelList = messageLabelMapper.qureyMessageLabelByMessageId(messageId);
            messageLabelDTO.setMessageId(messageId);
            //标签结果拼装
            queryLabelInfo(labelDTOList, messageLabelList);
            messageLabelDTO.setMessageLabelId(labelDTOList);
            dtoList.add(messageLabelDTO);
        }
        maps.put("resultCode", CODE_SUCCESS);
        maps.put("resultMsg", dtoList);
        return maps;
    }

    private void queryLabelInfo(List<LabelDTO> labelDTOList, List<MessageLabel> messageLabelList) {
        for (MessageLabel messageLabel : messageLabelList) {
            LabelDTO labelDTO = new LabelDTO();
            labelDTO.setInjectionLabelId(messageLabel.getInjectionLabelId());
            Label label = injectionLabelMapper.selectByPrimaryKey(messageLabel.getInjectionLabelId());
            if (label==null){
                continue;
            }
            labelDTO.setInjectionLabelName(label.getInjectionLabelName());
            labelDTOList.add(labelDTO);
        }
    }

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
        queryLabelInfo(labelDTOList, messageLabelList);
        messageLabelDTO.setMessageLabelId(labelDTOList);
        maps.put("resultCode", CODE_SUCCESS);
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
            MessageVO messageVO = BeanUtil.create(message,new MessageVO());
            Map<String,Object> labelMaps = qureyMessageLabel(message);
            if (labelMaps.get("resultCode").equals(CODE_SUCCESS)){
                MessageLabelDTO messageLabelDTO = (MessageLabelDTO)labelMaps.get("messageLabelDTO");
                List<LabelDTO> list = messageLabelDTO.getMessageLabelId();
                messageVO.setLabelDTOList(list);
            }
            messageVOList.add(messageVO);
        }
        maps.put("resultCode", CODE_SUCCESS);
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
        maps.put("resultCode", CODE_SUCCESS);
        maps.put("resultMsg", displayColumn.getDisplayColumnId());
        return maps;
    }

    /**
     * 查询出所有展示列
     */
    @Override
    public Map<String, Object> queryDisplays(String displayName,String displayType) {
        Map<String, Object> maps = new HashMap<>();
        List<DisplayColumnVO> displayColumnVOList = new ArrayList<>();
        List<DisplayColumn> displayColumnList = displayColumnMapper.findDisplayListByParam(displayName,displayType);
        for (DisplayColumn displayColumn : displayColumnList) {
            DisplayColumnVO displayColumnVO = BeanUtil.create(displayColumn,new DisplayColumnVO());
            displayColumnVOList.add(displayColumnVO);
        }
        maps.put("resultCode", CODE_SUCCESS);
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
            displayColumnLabel.setDisplayId(displayAllMessageReq.getDisplayColumnId());
            displayColumnLabel.setInjectionLabelId(injectionLabelId);
            displayColumnLabel.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            displayColumnLabel.setCreateStaff(UserUtil.loginId());
            displayColumnLabel.setUpdateStaff(UserUtil.loginId());
            displayColumnLabel.setCreateDate(DateUtil.getCurrentTime());
            displayColumnLabel.setUpdateDate(DateUtil.getCurrentTime());
            displayColumnLabel.setStatusDate(DateUtil.getCurrentTime());
            displayColumnLabelMapper.insert(displayColumnLabel);
        }
        DisplayColumn dc = new DisplayColumn();
        dc.setDisplayColumnId(displayAllMessageReq.getDisplayColumnId());
        return  queryLabelListByDisplayId(dc);
    }

    /**
     * 编辑展示列
     */
    @Override
    public Map<String, Object> viewDisplayColumn(DisplayAllMessageReq displayAllMessageReq) {
        Map<String, Object> map = new HashMap<>();
        Page page = displayAllMessageReq.getPage();
        PageHelper.startPage(page.getPage(),page.getPageSize());
        List<Long> injectionLabelIds = displayAllMessageReq.getInjectionLabelIds();
        List<Label> labelList = injectionLabelMapper.queryLabelsExceptSelected(injectionLabelIds,displayAllMessageReq.getLabelName());
        Page info = new Page(new PageInfo(labelList));
        List<LabelDTO> labelDTOList = new ArrayList<>();
        for (Label label : labelList) {
            LabelDTO labelDTO = new LabelDTO();
            labelDTO.setInjectionLabelId(label.getInjectionLabelId());
            labelDTO.setInjectionLabelName(label.getInjectionLabelName());
            labelDTOList.add(labelDTO);
        }
        map.put("resultCode", CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("labelList", labelDTOList);
        map.put("pageInfo",info);
        return map;
    }


}
