package com.zjtelcom.cpct.service.channel;

import com.ctzj.smt.bss.cpc.model.dto.MktResOrgRelDto;
import com.ctzj.smt.bss.cpc.write.service.api.IMktCouponRelService;
import com.ctzj.smt.bss.mktcenter.model.dto.ImportCouponInstReq;
import com.ctzj.smt.bss.mktcenter.model.dto.MktResCouponReq;
import com.zjtelcom.cpct.domain.channel.MktCamResource;
import com.zjtelcom.cpct.dto.channel.CamScriptAddVO;

import java.util.List;
import java.util.Map;

public interface CamElectronService {

    Map<String, Object> autoCreateCouponInst(Long resourceId,Long num);

    Map<String, Object> editCouponStatusCd(List<Long> resourceIdList, String type);

    Map<String,Object> addCouponOrgRelForMkt(MktResOrgRelDto param);

    Map<String,Object> addCouponProdAttrForMkt(MktResOrgRelDto param);

    Map<String,Object> delCouponOrgRelForMkt(MktResOrgRelDto param);

    Map<String,Object> delCouponProdAttrForMkt(MktResOrgRelDto param);

    Map<String, Object> qryChannelByOrgRelForMkt(Long resourceId);

    Map<String, Object> qrySelectProdByCouponForMkt(Long resourceId);

    Map<String,Object> publish4Mktcamresource( MktCamResource mktCamResource);








}
