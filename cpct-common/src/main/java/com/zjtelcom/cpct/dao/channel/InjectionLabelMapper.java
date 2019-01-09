package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.dto.grouping.SimpleInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface InjectionLabelMapper{

    int deleteByPrimaryKey(Long injectionLabelId);

    int insert(Label record);

    Label selectByPrimaryKey(Long injectionLabelId);

    Label selectByTagRowId(@Param("tagRowId") Long tagRowId);


    Label selectByLabelCode(@Param("labelCode") String  labelCode);

    List<Label> selectAll();

    List<Label> findByLabel(Label record);

    List<Label> selectByScope(@Param("scope") Long scope);

    List<Label> findLabelList(@Param("labelName")String labelName,@Param("fitDomain")String fitDomain,@Param("labelCode")String labelCode,@Param("scope")Integer scope,@Param("conditionType")String conditionType);

    List<Label> findByParam(@Param("labelName")String labelName);

    List<Label> findLabelListByCatalogId(@Param("catalogId")Long catalogId);

    int updateByPrimaryKey(Label record);

    int deleteAll();

    int insertBatch(@Param("record")List<Label> record);

    List<Label> queryTriggerByLeftOpers(@Param("record") List<Map<String, String>> record);

    List<Label> selectAllByCondition();

    List<Label> queryLabelsExceptSelected(@Param("injectionLabelIds") List<Long> injectionLabelIds,@Param("labelName")String labelName);

    List<Label> listLabelByIdList(@Param("injectionLabelIds") List<Long> injectionLabelIds);

    List<SimpleInfo> listLabelByCodeList(@Param("codeList") List<String> codeList);

    List<Label> listLabelByGrpId(@Param("grpId")Long grpId);

    List<Map<String,String>> listLabelByDisplayId(@Param("grpId")Long grpId);

}