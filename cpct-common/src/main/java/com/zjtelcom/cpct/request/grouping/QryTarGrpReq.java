package com.zjtelcom.cpct.request.grouping;

import java.io.Serializable;

/**
 * @Description 查询目标分群列表对象
 * @Author pengy
 * @Date 2018/7/1 18:20
 */
public class QryTarGrpReq implements Serializable{

    private static final long serialVersionUID = 7422623425318746490L;
    private Long tarGrpId;//目标分群标识
    private String tarGrpName;//目标分群名称
    private String tarGrpDesc;//目标分群的详细描述信息
    private String tarGrpType;//目标分群类型LOVB 客户、用户、销售品 等
    private String statusCd;//记录状态。1000有效 1100无效  1200	未生效 1300已归档  1001将生效  1002待恢复  1101将失效  1102待失效 1301	待撤消
    private Long createStaff;//创建人
    private Long updateStaff;//更新人
//    private PageInfo pageInfo;//分页

}
