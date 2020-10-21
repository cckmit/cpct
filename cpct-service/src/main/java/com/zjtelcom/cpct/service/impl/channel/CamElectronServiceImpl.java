package com.zjtelcom.cpct.service.impl.channel;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.cpc.model.common.CpcPage;
import com.ctzj.smt.bss.cpc.model.common.CpcResultObject;
import com.ctzj.smt.bss.cpc.model.dto.ChannelCouponDto;
import com.ctzj.smt.bss.cpc.model.dto.MktResOrgRelDto;
import com.ctzj.smt.bss.cpc.model.dto.ProductCouponDto;
import com.ctzj.smt.bss.cpc.query.service.api.IMktResOrgRelInfoService;
import com.ctzj.smt.bss.cpc.write.service.api.IMktCouponRelService;
import com.ctzj.smt.bss.mktcenter.coupon.service.api.ICouponInstImportDubboService;
import com.ctzj.smt.bss.mktcenter.coupon.service.api.IMktResBatchRecDubboService;
import com.ctzj.smt.bss.mktcenter.model.common.MktResResultObject;
import com.ctzj.smt.bss.mktcenter.model.dto.ImportCouponInstReq;
import com.ctzj.smt.bss.mktcenter.model.dto.MktResCouponReq;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.campaign.MktCamItemMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.MktCamResourceMapper;
import com.zjtelcom.cpct.dao.channel.MktProductAttrMapper;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.MktCamResource;
import com.zjtelcom.cpct.domain.channel.MktProductAttr;
import com.zjtelcom.cpct.dto.channel.MktCamResourceVO;
import com.zjtelcom.cpct.enums.ResourceTypeEnum;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.enums.SubTypeEnum;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.CamElectronService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class CamElectronServiceImpl extends BaseService implements CamElectronService {

    @Autowired(required = false)
    private ICouponInstImportDubboService instImportDubboService;
    @Autowired(required = false)
    private IMktResBatchRecDubboService batchRecDubboService;
    @Autowired(required = false)
    private IMktCouponRelService couponRelService;
    @Autowired(required = false)
    private IMktResOrgRelInfoService orgRelInfoService;
    @Autowired
    private MktCamResourceMapper mktCamResourceMapper;
    @Autowired
    private MktProductAttrMapper mktProductAttrMapper;
    @Autowired
    private MktCamItemMapper mktCamItemMapper;
    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private SysAreaMapper sysAreaMapper;

    @Override
    public Map<String, Object> listCampaign4Resource(MktCamResourceVO camResource) {
        Map<String,Object> result = new HashMap<>();
        MktCamResource mktCamResource = BeanUtil.create(camResource, new MktCamResource());
        //框架6400 自主6100
        mktCamResource.setFrameFlg(mktCamResource.getFrameFlg().equals("1") ? "(6100)" : "(6300，6400)");
        List<MktCamResource> list = mktCamResourceMapper.listPage(mktCamResource);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",list);
        return result;
    }

    @Override
    public Map<String, Object> listCampaignPage4Resource(MktCamResourceVO camResource) {
        Map<String,Object> result = new HashMap<>();
        PageHelper.startPage(camResource.getPage(),camResource.getPageSize());
        MktCamResource mktCamResource = BeanUtil.create(camResource, new MktCamResource());
        //框架6400 自主6100
        mktCamResource.setFrameFlg(mktCamResource.getFrameFlg().equals("1") ? "(6100)" : "(6300,6400)");
        List<MktCamResource> list = mktCamResourceMapper.listPage(mktCamResource);
        Page pageInfo = new Page(new PageInfo(list));
        List<MktCamResourceVO> resourceVOS = new ArrayList<>();
        for (MktCamResource resource : list) {
            MktCamResourceVO mktCamResourceVO = BeanUtil.create(resource, new MktCamResourceVO());
            String typeName = ResourceTypeEnum.getNameByCode(resource.getResourceType());
            String subType = SubTypeEnum.getNameByCode(resource.getResourceSubtype());
            mktCamResourceVO.setTypeString(typeName+"-"+subType);
            List<String> idList = ChannelUtil.StringToList(resource.getUseArea());
           List<String> list1 = new ArrayList<>();
            for (String aLong : idList) {
                SysArea sysArea = sysAreaMapper.selectByPrimaryKey(Integer.valueOf(aLong));
                list1.add(sysArea.getName());
            }
            MktCampaignDO campaignDO = mktCampaignMapper.selectByPrimaryKey(resource.getMktCampaignId());
            if (campaignDO!=null ){
                mktCamResourceVO.setMktCampaignNbr(campaignDO.getMktActivityNbr());
                mktCamResourceVO.setStaffName(campaignDO.getCreateStaff().toString());
            }
            mktCamResourceVO.setUseAreaInfo(ChannelUtil.list2String(list1,","));
            mktCamResourceVO.setStatusCd(StatusCode.getMsgByCode(resource.getStatusCd()));
            mktCamResourceVO.setGetStartTime(DateUtil.date2StringDate(resource.getGetEndTime()));
            mktCamResourceVO.setGetEndTime(DateUtil.date2StringDate(resource.getGetEndTime()));
            resourceVOS.add(mktCamResourceVO);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",resourceVOS);
        result.put("page",pageInfo);
        return result;
    }


    @Override
    public Map<String, Object> publish4Mktcamresource( MktCamResource mktCamResource) {
        Map<String,Object> result = new HashMap<>();
        Long staffId = UserUtil.getStaffId();
        //4.7	按指定数量自动生成电子券实例
        logger.info("【按指定数量自动生成电子券实例】--->>> 入参：" + JSON.toJSONString(mktCamResource.getResourceId()));
        Map<String, Object> map = autoCreateCouponInst(mktCamResource.getResourceId(), mktCamResource.getResourceApplyNum());
        logger.info("【按指定数量自动生成电子券实例】--->>> 出参：" + JSON.toJSONString(map));
        //4.8	电子券发布接口
        List<Long> idList = new ArrayList<>();
        idList.add(mktCamResource.getResourceId());
        editCouponStatusCd(idList,"1");
        //删除宽带券和渠道网点关联
        logger.info("【查询电子券关联渠道网点】--->>> 入参：" + JSON.toJSONString(mktCamResource.getResourceId()));
        CpcResultObject<CpcPage<ChannelCouponDto>> cpcPageCpcResultObject = orgRelInfoService.qryChannelByOrgRel(mktCamResource.getResourceId(), 1, 1000);
        logger.info("【查询电子券关联渠道网点】--->>> 出参：" + JSON.toJSONString(cpcPageCpcResultObject));
        CpcPage<ChannelCouponDto> resultObject = cpcPageCpcResultObject.getResultObject();
        List<ChannelCouponDto> datas = resultObject.getDatas();
        for (ChannelCouponDto data : datas) {
            CpcResultObject<String> stringCpcResultObject = couponRelService.delCouponOrgRel(data.getOrgRelId(), staffId);
            logger.info("【删除电子券关联渠道网点】--->>> 出参："+ data.getOrgRelId()+":"+ JSON.toJSONString(stringCpcResultObject));
        }
        //4.9	新增宽带券和渠道网点关联
        List<String> shopList = ChannelUtil.StringToList(mktCamResource.getDealShops());
        for (String shop : shopList) {
            MktResOrgRelDto mktResOrgRelDto = new MktResOrgRelDto();
            mktResOrgRelDto.setMktResId(mktCamResource.getResourceId());
            mktResOrgRelDto.setOrgId(Long.valueOf(shop));
            mktResOrgRelDto.setCreateStaff(staffId);
            logger.info("【新增宽带券和渠道网点关联】--->>> 入参：" + JSON.toJSONString(mktResOrgRelDto));
            CpcResultObject<String> stringCpcResultObject = couponRelService.addCouponOrgRel(mktResOrgRelDto);
            logger.info("【新增宽带券和渠道网点关联】--->>> 出参：" + JSON.toJSONString(stringCpcResultObject));
        }
        //删除宽带券和产品属性关联
        CpcResultObject<CpcPage<ProductCouponDto>> object2 = orgRelInfoService.qrySelectProdByCoupon(mktCamResource.getResourceId(), 1, 1000);
        logger.info("【查询宽带券和产品属性关联】--->>> 出参：" + JSON.toJSONString(object2));
        List<ProductCouponDto> datas2 = object2.getResultObject().getDatas();
        for (ProductCouponDto data : datas2) {
            CpcResultObject<String> object = couponRelService.delCouponProdAttr(data.getMktResAttrId(), staffId);
            logger.info("【删除宽带券和产品属性关联】--->>> 出参："+ data.getMktResAttrId()+":"+ JSON.toJSONString(object));
        }
        //4.10 新增宽带券和产品属性依赖
        List<String> productItemList = ChannelUtil.StringToList(mktCamResource.getDependProductId());
        for (String product : productItemList) {
            MktProductAttr attrParam = new MktProductAttr();
            attrParam.setProductId(Long.valueOf(product));
            attrParam.setRuleId(mktCamResource.getRuleId());
            attrParam.setFrameFlg(mktCamResource.getFrameFlg());
            List<MktProductAttr> productAttrs = mktProductAttrMapper.selectByProduct(attrParam);
            for (MktProductAttr attr : productAttrs) {
                MktCamItem item = mktCamItemMapper.selectByPrimaryKey(attr.getProductId());
                if (item!=null){
                    CpcResultObject<String> object = couponRelService.addCouponProdAttr(item.getOfferCode()
                            , mktCamResource.getResourceId(), staffId, attr.getAttrId());
                    logger.info("【新增宽带券和产品属性依赖】--->>> 出参："+ item.getOfferCode()+":"+ JSON.toJSONString(object));
                }
            }
        }
        return null;
    }


    /**
     * 生成实例
     * @param resourceId
     * @param num
     * @return
     */
    @Override
    public Map<String, Object> autoCreateCouponInst(Long resourceId,Long num) {
        Map<String,Object> result = new HashMap<>();
        Long staffId = UserUtil.getStaffId();
        ImportCouponInstReq param = new ImportCouponInstReq();
        param.setStaffId(staffId);
        param.setMktResId(resourceId);
        param.setQuantity(num);
        MktResResultObject<Boolean> booleanMktResResultObject = instImportDubboService.autoCreateCouponInst(param);
        result.put("return",booleanMktResResultObject);
        return result;
    }

    /**
     * 发布/撤销
     * @param resourceIdList
     * @param type 0撤销,1发布
     * @return
     */
    @Override
    public Map<String, Object> editCouponStatusCd(List<Long> resourceIdList,String type) {
        Long staffId = UserUtil.getStaffId();
        MktResCouponReq param = new MktResCouponReq();
        param.setStaffId(staffId);
        param.setMktResIds(resourceIdList);
        param.setType(type);
        batchRecDubboService.editCouponStatusCd(param);
        return null;
    }

    @Override
    public Map<String, Object> addCouponOrgRelForMkt(MktResOrgRelDto param) {
        couponRelService.addCouponOrgRel(param);
        return null;
    }

    @Override
    public Map<String, Object> addCouponProdAttrForMkt(MktResOrgRelDto param) {

        //String var1, Long var2, Long var3, Long var4
//        couponRelService.addCouponProdAttr();
        return null;
    }

    @Override
    public Map<String, Object> delCouponOrgRelForMkt(MktResOrgRelDto param) {
//        couponRelService.delCouponOrgRel();
        return null;
    }

    @Override
    public Map<String, Object> delCouponProdAttrForMkt(MktResOrgRelDto param) {
//        couponRelService.delCouponProdAttr();
        return null;
    }

    @Override
    public Map<String, Object> qryChannelByOrgRelForMkt(Long resourceId) {
        Long staffId = UserUtil.getStaffId();
        CpcResultObject<CpcPage<ChannelCouponDto>> cpcPageCpcResultObject = orgRelInfoService.qryChannelByOrgRel(resourceId, 1, 1000);
        CpcPage<ChannelCouponDto> resultObject = cpcPageCpcResultObject.getResultObject();
        List<ChannelCouponDto> datas = resultObject.getDatas();
        for (ChannelCouponDto data : datas) {
            couponRelService.delCouponOrgRel(data.getOrgRelId(),staffId);
        }
        return null;
    }

    @Override
    public Map<String, Object> qrySelectProdByCouponForMkt(Long resourceId) {
        Long staffId = UserUtil.getStaffId();
        CpcResultObject<CpcPage<ProductCouponDto>> cpcPageCpcResultObject = orgRelInfoService.qrySelectProdByCoupon(resourceId, 1, 1000);
        List<ProductCouponDto> datas = cpcPageCpcResultObject.getResultObject().getDatas();
        for (ProductCouponDto data : datas) {
            couponRelService.delCouponProdAttr(data.getMktResAttrId(),staffId);
        }
        return null;
    }

}
