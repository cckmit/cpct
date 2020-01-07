package com.zjtelcom.cpct.service.impl.report;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.report.MktCamTopicMapper;
import com.zjtelcom.cpct.domain.report.TopicDO;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.report.TopicManagerService;
import com.zjtelcom.cpct.util.EsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class TopicManagerServiceImpl implements TopicManagerService {

    @Autowired
    private MktCamTopicMapper mktCamTopicMapper;
    //  year,season,topicName,description
    @Override
    public Map<String, Object> addTopic(Map<String, Object> topicContent) {
        TopicDO topicDO = new TopicDO();
        Map<String, Object> result = new HashMap<>();
        Integer affectRow = 0;
        try {
            if (topicContent.containsKey("year")){
                topicDO.setYear((String)topicContent.get("year"));
            }
            if (topicContent.containsKey("season")){
                topicDO.setSeason((String)topicContent.get("season"));
            }
            if (topicContent.containsKey("topicName")){
                topicDO.setTopicName((String)topicContent.get("topicName"));
            }
            if (topicContent.containsKey("description")){
                topicDO.setDescription((String)topicContent.get("description"));
            }
            //生成主题编码
            String randomCode = EsUtil.getRandomStr(4);
            topicDO.setTopicCode(randomCode);

            topicDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            topicDO.setStatusDate(new Date());
            //创建时间
//            topicDO.setCreateStaff(new Long("1111"));
            topicDO.setCreateDate(new Date());
            //更新
//            topicDO.setUpdateStaff(new Long("2222"));
            topicDO.setUpdateDate(new Date());
            affectRow = mktCamTopicMapper.insertTopic(topicDO);

        }catch (Exception e){
            e.printStackTrace();
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg","添加失败，请重新操作");
            return result;
        }

        result.put("resultCode", CommonConstant.CODE_SUCCESS);

        Map<String,Object> seasonTranfer = new HashMap<>();
        seasonTranfer.put("1000","一季度");
        seasonTranfer.put("2000","二季度");
        seasonTranfer.put("3000","三季度");
        seasonTranfer.put("4000","四季度");
        topicDO.setSeason(sesonInChinese(topicDO));
        result.put("content",topicDO);
        return result;
    }

    private String sesonInChinese(TopicDO topicDO){
        Map<String,Object> seasonTranfer = new HashMap<>();
        seasonTranfer.put("1000","一季度");
        seasonTranfer.put("2000","二季度");
        seasonTranfer.put("3000","三季度");
        seasonTranfer.put("4000","四季度");
        String season =(String)seasonTranfer.get(topicDO.getSeason());
        return  season;
    }

    //    拼接topicName
    private String toNewTopicName(TopicDO topicDO){
        String newTopicName = "";
        if(topicDO.getSeason()!= ""){
            Map<String,Object> seasonTranfer = new HashMap<>();
            seasonTranfer.put("1000","一季度");
            seasonTranfer.put("2000","二季度");
            seasonTranfer.put("3000","三季度");
            seasonTranfer.put("4000","四季度");
            newTopicName = topicDO.getTopicName() + topicDO.getYear() + seasonTranfer.get(topicDO.getSeason());
        }else {
            newTopicName = topicDO.getTopicName() +  topicDO.getYear();
        }
        return  newTopicName;
    }
    @Override
    public Map<String, Object> updateTopic(Map<String, Object> topicContent) {
        Map<String,Object> result = new HashMap<>();
        TopicDO topicDO = new TopicDO();
        int affectRow = 0;
        try {
            if (topicContent.containsKey("topicId")){
                topicDO.setTopicId((int)topicContent.get("topicId"));
            }
            if (topicContent.containsKey("year")){
                topicDO.setYear((String)topicContent.get("year"));
            }
            if (topicContent.containsKey("season")){
                topicDO.setSeason((String)topicContent.get("season"));
            }
            if (topicContent.containsKey("topicName")){
                topicDO.setTopicName((String)topicContent.get("topicName"));
            }
            if (topicContent.containsKey("description")){
                topicDO.setDescription((String) topicContent.get("description"));
            }
            if(topicContent.containsKey("statusCd")){
                topicDO.setStatusCd((String)topicContent.get("statusCd"));
                topicDO.setStatusDate(new Date());
            }
            topicDO.setUpdateDate(new Date());

            affectRow = mktCamTopicMapper.updateTopic(topicDO);

        }catch (Exception e){
            e.printStackTrace();
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg","更新失败");
            return result;
        }
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        topicDO.setSeason(sesonInChinese(topicDO));
        result.put("conntent",topicDO);
        return result;
    }

    @Override
    public Map<String, Object> updateTopicState(Map<String, Object> updateTopicState) {
        Map<String,Object> result = new HashMap<String, Object>();
        TopicDO topicDO = new TopicDO();
        try {
            topicDO.setTopicId((int)updateTopicState.get("topicId"));
            topicDO.setStatusCd((String)updateTopicState.get("stateCd"));
            topicDO.setStatusDate(new Date());
            topicDO.setUpdateDate(new Date());
            mktCamTopicMapper.updateTopicState(topicDO);
        }catch (Exception e){
            e.printStackTrace();
            result.put("stateCode", CommonConstant.CODE_FAIL);
            result.put("stateMsg","状态更新失败");
        }

        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("conntent",topicDO);
        return result;
    }

    @Override
    public Map<String, Object> deleteTopic(int id) {

        Map<String,Object> result = new HashMap<>();
        int affectRow = 0;

        try {
            affectRow = mktCamTopicMapper.deleteTopicById(id);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (affectRow == 0){
            result.put("isSuccess",0);
            return result;
        }else {
            result.put("resultCode", CommonConstant.CODE_SUCCESS);
            result.put("content","删除成功");
            return result;
        }
    }

    @Override
    public Map<String, Object> getTopicInfoById(int id) {
        TopicDO topicDO = new TopicDO();
        Map<String,Object> result = new HashMap<>();
        try {
            topicDO = mktCamTopicMapper.selectTopicInfoById(id);

        }catch (Exception e){
            e.printStackTrace();
        }
        if (topicDO == null){
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg","id不存在");
            return result;
        }else{
            Map<String,Object> tmp = new HashMap<>();
            List< Map<String,Object>> contentList = new ArrayList<>();
            tmp.put("topicId",topicDO.getTopicId());
            tmp.put("year",topicDO.getYear());
            tmp.put("season",topicDO.getSeason());
            tmp.put("topicName",topicDO.getTopicName());
            tmp.put("topicCode",topicDO.getTopicCode());
            tmp.put("description",topicDO.getDescription());
            tmp.put("state",topicDO.getStatusCd());
            contentList.add(tmp);

            result.put("resultCode", CommonConstant.CODE_SUCCESS);
            result.put("content",contentList);
            return result;
        }
    }

    @Override
    public Map<String, Object> getAllTopic() {
        List<Map<String, Object>> resultLists = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        try {
            List<TopicDO> topicLists = mktCamTopicMapper.selectAllTopicInfo();
            for (TopicDO topicDO : topicLists) {
                Map<String, Object> temp = new HashMap<>();
                temp.put("id", topicDO.getTopicId());
                temp.put("year", topicDO.getYear());
                temp.put("season", topicDO.getSeason());
                temp.put("topicName", topicDO.getTopicName());
                temp.put("topicCode", topicDO.getTopicCode());
                temp.put("stateCd", topicDO.getStatusCd());
                temp.put("description", topicDO.getDescription());
                temp.put("seasonChinese",sesonInChinese(topicDO));
                resultLists.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (resultLists.size() == 0) {
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg", "主题列表为空");
            return result;
        } else {
            result.put("resultCode", CommonConstant.CODE_SUCCESS);
            result.put("content",resultLists);
            return result;
        }
    }

    //分页获取主题列表
    @Override
    public Map<String, Object> getTopicPageLists(Map<String, Object> pageParams) {
        Map<String,Object> result = new HashMap<>();
        List<TopicDO> topicLists = new ArrayList<>();
        String pageSize ="10";//默认大小
        if (pageParams.get("pageSize").toString() != ""){
            pageSize = pageParams.get("pageSize").toString();
        }
        try {
            PageHelper.startPage(Integer.parseInt(pageParams.get("page").toString()),Integer.parseInt(pageSize));
//            模糊搜索
            topicLists =  mktCamTopicMapper.selectByKey((String)pageParams.get("year"),(String)pageParams.get("season"),(String)pageParams.get("topicName"),(String)pageParams.get("topicCode"));
            result.put("resultCode", CommonConstant.CODE_SUCCESS);
            result.put("resultMsg","请求成功");
            result.put("topicList",topicLists);
            result.put("pageInfo",new Page(new PageInfo<>(topicLists)));

        }catch (Exception e){
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg","请求失败");
            e.printStackTrace();
        }finally {
            return result;
        }

    }


}
