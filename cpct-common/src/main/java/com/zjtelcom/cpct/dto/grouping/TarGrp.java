package com.zjtelcom.cpct.dto.grouping;

import com.zjtelcom.cpct.domain.grouping.TarGrpDetailDO;
import lombok.Data;

/**
 * @Description TarGrp
 * @Author pengy
 * @Date 2018/6/25 11:29
 */
@Data
public class TarGrp extends TarGrpDetailDO {

    private String actType;//  KIP=保持/ADD=新增/MOD=修改/DEL=删除

}
