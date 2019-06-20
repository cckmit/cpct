package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.domain.channel.DisplayColumn;
import com.zjtelcom.cpct.domain.channel.Message;
import com.zjtelcom.cpct.dto.channel.ProductParam;
import com.zjtelcom.cpct.request.channel.DisplayAllMessageReq;
import com.zjtelcom.cpct.request.channel.MessageReq;

import java.util.List;
import java.util.Map;

/**
 * 信息关联标签service
 *
 * @author pengyu
 */
public interface MessageLabelService {

    /**
     * 通过信息idList查询标签列表
     */
    Map<String,Object> qureyLabelListByIdList(ProductParam labelIdList);

    /**
     * 通过信息id查询出关联到的标签
     */
    Map<String,Object> qureyMessageLabel(Message message);


    /**
     * 通过信息id查询出关联到的标签
     */
    Map<String,Object> qureyMessageLabelByMessageIdList(MessageReq req);

    /**
     * 查询出所有信息
     */
    Map<String,Object> queryMessages(String displayColumnType);

    /**
     * 新增标签组
     */
    Map<String,Object> createLabelGroup(DisplayColumn displayColumn);

    /**
     * 查询出所有展示列
     */
    Map<String,Object> queryDisplays(String displayName,String displayType);

    /**
     * 保存展示列所有信息
     */
    Map<String,Object> createDisplayAllMessage(DisplayAllMessageReq displayAllMessageReq);

    /**
     * 编辑展示列
     */
    Map<String,Object> viewDisplayColumn(DisplayAllMessageReq displayAllMessageReq);

    /**
     * 删除展示列
     */
    Map<String,Object> delDisplayColumn( DisplayAllMessageReq req);

    /**
     * 删除展示列标签关联
     */
    Map<String,Object> delColumnLabelRel(Long displayId,Long labelId);

    /**
     * 获取展示列标签列表
     */
    Map<String,Object> queryLabelListByDisplayId(DisplayColumn req);

    /**
     * 展示列标签展示类型配置
     */
    Map<String, Object> configureLabelDisplayType(Long displayColumnId, Long labelId, List<String> labelDisplayTypeId);

    /**
     * 查询展示列标签展示类型
     */
    Map<String, Object> viewLabelDisplayType(Long displayColumnId, Long labelId);
}
