package com.zjtelcom.cpct.service.impl.event;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.ContactChannelMapper;
import com.zjtelcom.cpct.dao.event.EventSorceMapper;
import com.zjtelcom.cpct.dao.event.InterfaceCfgMapper;
import com.zjtelcom.cpct.domain.channel.Channel;
import com.zjtelcom.cpct.domain.event.EventSorceDO;
import com.zjtelcom.cpct.domain.event.InterfaceCfg;
import com.zjtelcom.cpct.dto.event.EventSorce;
import com.zjtelcom.cpct.dto.event.InterfaceCfgVO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.InterfaceCfgService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import io.netty.channel.group.ChannelMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class InterfaceCfgServiceImpl extends BaseService implements InterfaceCfgService {

    @Autowired
    private InterfaceCfgMapper interfaceCfgMapper;
    @Autowired
    private EventSorceMapper eventSorceMapper;
    @Autowired
    private ContactChannelMapper channelMapper;


    @Override
    public Map<String, Object> createInterfaceCfg(InterfaceCfg interfaceCfg) {
        Map<String,Object> result = new HashMap<>();
        if (interfaceCfg.getEvtSrcId()==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","请选择事件源");
            return result;
        }
        EventSorceDO eventSorce = eventSorceMapper.selectByPrimaryKey(interfaceCfg.getEvtSrcId());
        if (eventSorce==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","事件源不存在");
            return result;
        }
        InterfaceCfg ic = BeanUtil.create(interfaceCfg,new InterfaceCfg());
        ic.setCreateDate(DateUtil.getCurrentTime());
        ic.setUpdateDate(DateUtil.getCurrentTime());
        ic.setStatusDate(DateUtil.getCurrentTime());
        ic.setUpdateStaff(UserUtil.loginId());
        ic.setCreateStaff(UserUtil.loginId());
        ic.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        interfaceCfgMapper.insert(ic);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String, Object> modInterfaceCfg(InterfaceCfg interfaceCfg) {
        Map<String,Object> result = new HashMap<>();
        InterfaceCfg ic = interfaceCfgMapper.selectByPrimaryKey(interfaceCfg.getInterfaceCfgId());
        if (ic==null ){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","事件源接口不存在");
            return result;
        }
        BeanUtil.copy(interfaceCfg,ic);
        ic.setUpdateDate(DateUtil.getCurrentTime());
        ic.setUpdateStaff(UserUtil.loginId());
        interfaceCfgMapper.updateByPrimaryKey(ic);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","编辑成功");
        return result;
    }

    @Override
    public Map<String, Object> delInterfaceCfg(InterfaceCfg interfaceCfg) {
        Map<String,Object> result = new HashMap<>();
        InterfaceCfg ic = interfaceCfgMapper.selectByPrimaryKey(interfaceCfg.getInterfaceCfgId());
        if (ic==null ){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","事件源接口不存在");
            return result;
        }
        interfaceCfgMapper.deleteByPrimaryKey(ic.getInterfaceCfgId());
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");
        return result;
    }

    @Override
    public Map<String, Object> listInterfaceCfg(Long evtSrcId,String interfaceName,String interfaceType,Integer page,Integer pageSize){
        Map<String,Object> result = new HashMap<>();
        PageHelper.startPage(page,pageSize);
        List<InterfaceCfg> cfgList = interfaceCfgMapper.findInterfaceCfgListByParam(evtSrcId,interfaceName,interfaceType);
        Page info = new Page(new PageInfo(cfgList));
        List<InterfaceCfgVO> voList = new ArrayList<>();
        for (InterfaceCfg interfaceCfg1 : cfgList){
            EventSorceDO eventSorce = eventSorceMapper.selectByPrimaryKey(interfaceCfg1.getEvtSrcId());
            if (eventSorce==null){
                continue;
            }
            InterfaceCfgVO vo = BeanUtil.create(interfaceCfg1,new InterfaceCfgVO());
            vo.setEvtSrcName(eventSorce.getEvtSrcName());
            Channel caller = channelMapper.selectByPrimaryKey(Long.valueOf(interfaceCfg1.getCaller()));
            if (caller!=null){
                vo.setCallerName(caller.getContactChlName());
            }
            Channel provider = channelMapper.selectByPrimaryKey(Long.valueOf(interfaceCfg1.getProvider()));
            if (provider!=null){
                vo.setProviderName(provider.getContactChlName());
            }
            voList.add(vo);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",voList);
        result.put("page",info);
        return result;
    }

    @Override
    public Map<String, Object> getInterfaceCfgDetail(InterfaceCfg interfaceCfg) {
        Map<String,Object> result = new HashMap<>();
        InterfaceCfg ic = interfaceCfgMapper.selectByPrimaryKey(interfaceCfg.getInterfaceCfgId());
        if (ic==null ){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","事件源接口不存在");
            return result;
        }
        InterfaceCfgVO vo = BeanUtil.create(ic,new InterfaceCfgVO());
        EventSorceDO eventSorce = eventSorceMapper.selectByPrimaryKey(ic.getEvtSrcId());
        if (eventSorce!=null){
            vo.setEvtSrcName(eventSorce.getEvtSrcName());
        }
        Channel caller = channelMapper.selectByPrimaryKey(Long.valueOf(ic.getCaller()));
        if (caller!=null){
            vo.setCallerName(caller.getContactChlName());
        }
        Channel provider = channelMapper.selectByPrimaryKey(Long.valueOf(ic.getProvider()));
        if (provider!=null){
            vo.setProviderName(provider.getContactChlName());
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",vo);
        return result;
    }
}