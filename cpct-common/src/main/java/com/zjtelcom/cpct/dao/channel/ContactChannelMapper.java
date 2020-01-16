package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.Channel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ContactChannelMapper {
    int deleteByPrimaryKey(Long contactChlId);

    int insert(Channel record);

    Channel selectByPrimaryKey(@Param("contactChlId")Long contactChlId);

    Channel selectChannel4AllChannel(Long contactChlId);

    List<Channel> selectAll(@Param("channelName")String channelName);

    List<Channel> selectByType(@Param("channelType")String channelType);

    List<Channel> findParentList();

    List<Channel> findChildListByParentId(@Param("parentId")Long parentId, @Param("triggerType")String triggerType);

    int updateByPrimaryKey(Channel record);

    List<Channel> findChildList();

    Channel selectByCode(@Param("code")String code);

    List<Channel> queryList(Channel record);

    List<String> selectChannelCodeByPrimaryKey(List<Long> contactChlIdList);

    List<Channel> selectBatchByCode(@Param("contactChlCodeList") List<String> contactChlCodeList);

    List<Channel> getRealTimeChannel();

    List<Channel> getBatchChannel();

    List<Channel> getNewActivityChannel();

    List<Channel> findChildListByTriggerType();

    // 集团和省下发触点渠道映射
    Channel getMappingCode(@Param("code")String groupChannelCode);
}