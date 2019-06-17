package com.zjtelcom.cpct.service.impl.channel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.domain.channel.MktResource;
import com.zjtelcom.cpct.service.channel.ResourceService;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct_prod.dao.offer.MktResourceProdMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private MktResourceProdMapper mktResourceMapper;

    @Override
    public Map<String, Object> getResourceListByName(Map<String,Object> params) {
        Map<String,Object> result = new HashMap<>();
        List<MktResource> mktResourceList = new ArrayList<>();
        Integer page = MapUtil.getIntNum(params.get("page"));
        Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
        String mktResName = MapUtil.getString(params.get("mktResName"));
        List<Long> resourceIdList = (List<Long>) params.get("resourceIdList");
        PageHelper.startPage(page,pageSize);
        mktResourceList = mktResourceMapper.selectByResourceName(mktResName,resourceIdList);
        Page pageInfo = new Page(new PageInfo(mktResourceList));
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",mktResourceList);
        result.put("page",pageInfo);
        return result;
    }
}
