package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.domain.channel.DisplayColumn;
import com.zjtelcom.cpct.domain.channel.Message;
import com.zjtelcom.cpct.request.channel.DisplayAllMessageReq;
import java.util.Map;

/**
 * 信息关联标签service
 *
 * @author pengyu
 */
public interface MessageLabelService {

    /**
     * 通过信息id查询出关联到的标签
     */
    Map<String,Object> qureyMessageLabel(Message message);

    /**
     * 查询出所有信息
     */
    Map<String,Object> queryMessages();

    /**
     * 新增标签组
     */
    Map<String,Object> createLabelGroup(DisplayColumn displayColumn);

    /**
     * 查询出所有展示列
     */
    Map<String,Object> queryDisplays();

    /**
     * 保存展示列所有信息
     */
    Map<String,Object> createDisplayAllMessage(DisplayAllMessageReq displayAllMessageReq);

    /**
     * 编辑展示列
     */
    Map<String,Object> viewDisplayColumn(DisplayAllMessageReq displayAllMessageReq);

}
