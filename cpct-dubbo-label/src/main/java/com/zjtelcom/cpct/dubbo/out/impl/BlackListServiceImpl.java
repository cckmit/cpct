package com.zjtelcom.cpct.dubbo.out.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.zjtelcom.cpct.bean.ResponseVO;
import com.zjtelcom.cpct.dao.blacklist.BlackListLogMapper;
import com.zjtelcom.cpct.dao.blacklist.BlackListMapper;
import com.zjtelcom.cpct.domain.blacklist.BlackListDO;
import com.zjtelcom.cpct.domain.blacklist.BlackListLogDO;
import com.zjtelcom.cpct.dubbo.out.BlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@Component
public class BlackListServiceImpl implements BlackListService {

    @Autowired
    BlackListMapper blackListMapper;
    @Autowired
    ResponseVO responseVO;
    @Autowired
    BlackListLogMapper blackListLogMapper;
    private static final String SUCCESS_CODE = "0";
    private static final String FAIL_CODE = "1";

    private com.zjtelcom.cpct.service.blacklist.BlackListService blackListService;

    @Override
    public Map<String, Object> addBlackList(List<Map<String, Object>> blackListContent) {
        Map<String,Object> result = new HashMap<>();
        try {
            for(Map<String,Object> map: blackListContent){
                BlackListDO blackListDO = new BlackListDO();
                blackListDO.setAssetPhone((String)map.get("assetPhone"));
                blackListDO.setServiceCate((String)map.get("serviceCate"));
                blackListDO.setMaketingCate((String)map.get("maketingCate"));
                blackListDO.setPublicBenefitCate((String)map.get("publicBenefitCate"));
                blackListDO.setChannel((String)map.get("channel"));
                blackListDO.setStaffId((String)map.get("staffId"));
                blackListDO.setOperType((String)map.get("operType"));

                String phone = (String)map.get("assetPhone");
                List<String> phoneList = new ArrayList<>();
                phoneList.add(phone);
                List<BlackListDO> blackListDOS = blackListMapper.getBlackListById(phoneList);
                if (blackListDOS.size() == 0){
                    //添加黑名单
                    blackListDO.setCreateDate(new Date());
                    blackListMapper.addBlackList(blackListDO);
                    //添加操作日志
                    BlackListLogDO blackListLogDO = new BlackListLogDO();
                    blackListLogDO.setMethod("add");
                    blackListLogDO.setAssetPhone((String)map.get("assetPhone"));
                    blackListLogDO.setServiceCate((String)map.get("serviceCate"));
                    blackListLogDO.setMaketingCate((String)map.get("maketingCate"));
                    blackListLogDO.setPublicBenefitCate((String)map.get("publicBenefitCate"));
                    blackListLogDO.setChannel((String)map.get("channel"));
                    blackListLogDO.setStaffId((String)map.get("staffId"));
                    blackListLogDO.setOperType((String)map.get("operType"));
                    blackListLogMapper.addBlacklistlog(blackListLogDO);
                }else{
                    //更新黑名单
                    blackListDO.setUpdateDate(new Date());
                    blackListMapper.updateBlackList(blackListDO);
                    //添加操作日志
                    BlackListLogDO blackListLogDO = new BlackListLogDO();
                    blackListLogDO.setMethod("update");
                    blackListLogDO.setAssetPhone((String)map.get("assetPhone"));
                    blackListLogDO.setServiceCate((String)map.get("serviceCate"));
                    blackListLogDO.setMaketingCate((String)map.get("maketingCate"));
                    blackListLogDO.setPublicBenefitCate((String)map.get("publicBenefitCate"));
                    blackListLogDO.setChannel((String)map.get("channel"));
                    blackListLogDO.setStaffId((String)map.get("staffId"));
                    blackListLogDO.setOperType((String)map.get("operType"));
                    blackListLogMapper.addBlacklistlog(blackListLogDO);
                }
            }
            return responseVO.response(SUCCESS_CODE,"编辑黑名单成功");

        }catch (Exception e){
            e.printStackTrace();
            return responseVO.response(FAIL_CODE,"编辑黑名单失败");
        }
    }

    @Override
    public Map<String, Object> deleteBlackList(List<String> phoneNumsDeleted) {
        try {
            blackListMapper.deleteBlackListById(phoneNumsDeleted);

            //添加操作日志
            for(String phone: phoneNumsDeleted){
                BlackListLogDO blackListLogDO = new BlackListLogDO();
                blackListLogDO.setMethod("delete");
                blackListLogDO.setAssetPhone(phone);
                blackListLogMapper.addBlacklistlog(blackListLogDO);
            }
            return responseVO.response(SUCCESS_CODE,"删除黑名单成功");
        }catch (Exception e){
            e.printStackTrace();
            return responseVO.response(FAIL_CODE,"删除黑名单失败");
        }
    }

    @Override
    public Map<String, Object> getBlackListById(List<String> phoneNums) {
        try {
            List<BlackListDO> blackListDOList = blackListMapper.getBlackListById(phoneNums);
            //添加操作日志
            for(String phone: phoneNums){
                BlackListLogDO blackListLogDO = new BlackListLogDO();
                blackListLogDO.setMethod("get");
                blackListLogDO.setAssetPhone(phone);
                blackListLogMapper.addBlacklistlog(blackListLogDO);
            }
            return responseVO.response(SUCCESS_CODE,"获取黑名单成功",blackListDOList);
        }catch (Exception e){
            e.printStackTrace();
            return responseVO.response(FAIL_CODE,"获取黑名单失败");
        }
    }

    @Override
    public Map<String, Object> getAllBlackList() {
        try {
            List<BlackListDO> blackListDOList = blackListMapper.getAllBlackList();

            //添加操作日志
            BlackListLogDO blackListLogDO = new BlackListLogDO();
            blackListLogDO.setMethod("getAll");
            blackListLogMapper.addBlacklistlog(blackListLogDO);
            return responseVO.response(SUCCESS_CODE,"获取黑名单成功",blackListDOList);
        }catch (Exception e){
            e.printStackTrace();
            return responseVO.response(FAIL_CODE,"获取黑名单失败");
        }
    }

    /**
     *
     *
     * @return
     */
    @Override
    public Map<String, Object> exportBlackListFile() {
        return blackListService.exportBlackListFile();
    }

    @Override
    public Map<String, Object> importBlackListFile() {
        return blackListService.importBlackListFile();
    }

}
