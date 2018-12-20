package com.zjtelcom.cpct.open.base.common;

import com.github.pagehelper.PageHelper;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.exception.ValidateException;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/10/31
 * @Description:业务相关 公用类
 */
public class CommonUtil {

    /**
     * 对分页请求入参设置page和pageSize
     * @param params  默认首页  每页返回30条数据
     */
    public static void setPage(Map<String, Object> params){
        int page=1;
        int pageSize=30;
        if(StringUtils.isNotBlank((String) params.get("offset"))){
            page= Integer.valueOf((String)params.get("offset")) ;
        }
        if(StringUtils.isNotBlank((String) params.get("limit"))){
            pageSize= Integer.valueOf((String)params.get("limit")) ;
        }
        PageHelper.startPage(page, pageSize);
    }


    /**
     * 输入参数转为long
     * @param id
     * @return
     */
    public static Long stringToLong(String id){
        Long l=0L;
        if(StringUtils.isNotBlank(id)){
            l=Long.valueOf(id);
        }else{
            throw new ValidateException();
        }
        return l;
    }
}
