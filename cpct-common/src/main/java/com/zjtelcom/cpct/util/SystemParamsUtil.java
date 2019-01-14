package com.zjtelcom.cpct.util;

import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.system.SysParams;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Auther: anson
 * @Date: 2018/12/20
 * @Description:静态参数工具类
 */
@Component
public class SystemParamsUtil {

    private static String IS_OPEN_SYNC="";          //是否开启同步  0不开启同步  1开启同步(所有模块的同步)   2开启同步(但是事件和活动模块同步功能关闭)

    public static String SYNC_VALUE="IS_OPEN_SYNC"; //数据库中系统参数表同步开关的key





    /**
     * 获取同步开关状态
     * @return
     */
    public synchronized static String getSyncValue(){
        if(StringUtils.isBlank(IS_OPEN_SYNC)){
            SysParamsMapper sysParamsMapper=SpringUtil.getBean(SysParamsMapper.class);
            List<SysParams> sysParams = sysParamsMapper.listParamsByKeyForCampaign(SYNC_VALUE);
            if (sysParams.isEmpty()) {
                //如果数据库没有值 则返回默认开启
                IS_OPEN_SYNC="1";
            }else{
                if ("1".equals(sysParams.get(0).getParamValue())) {
                    IS_OPEN_SYNC = "1";
                }else if("2".equals(sysParams.get(0).getParamValue())){
                    //除了事件和活动  其他的模块都开启同步功能
                    IS_OPEN_SYNC="2";
                }else{
                    //不开启同步
                    IS_OPEN_SYNC="0";
                }
            }
            return IS_OPEN_SYNC;
        }
        return IS_OPEN_SYNC;
    }

    /**
     * 针对除活动和事件外的同步的判断方法  目前值为  1和2 都可以同步
     * @return
     */
    public synchronized static boolean isSync(){
        boolean tip=false;
        if (SystemParamsUtil.getSyncValue().equals("1")||SystemParamsUtil.getSyncValue().equals("2")){
            tip=true;
        }
        return  tip;
    }

    /**
     * 针对活动和事件的同步的判断方法     目前只有  值为1才能同步
     * @return
     */
    public synchronized static boolean isCampaignSync(){
        boolean tip=false;
        if (SystemParamsUtil.getSyncValue().equals("1")){
            tip=true;
        }
        return  tip;
    }




    /**
     * 初始化同步值
     */
    public static void initValue(){
        IS_OPEN_SYNC="";
    }







}
