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

    private static String IS_OPEN_SYNC="";          //是否开启同步 0不开启  1开启

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
     * 初始化同步值
     */
    public static void initValue(){
        IS_OPEN_SYNC="";
    }







}
