package com.zjtelcom.cpct.open.entity.tarGrp;

/**
 * @Auther: anson
 * @Date: 2018-11-05 12:21:00
 * @Description:试算目标分群 实体类 匹配集团openapi规范返回
 */
public class OpenTarGrp {

    private Integer tarGrpId;//目标分群标识
    private String tarGrpName;//目标分群名称
    private String tarGrpDesc;//目标分群的详细描述信息
    private String tarGrpType;//目标分群类型,1000客户 2000产品实例 3000	销售品 4000营销资源 5000礼包
    private String statusCd;//记录状态。1000有效 1100无效  1200	未生效 1300已归档  1001将生效  1002待恢复  1101将失效  1102待失效 1301	待撤消
    private String statusDate;//状态时间
    private String remark;//备注

    public Integer getTarGrpId() {
        return tarGrpId;
    }

    public void setTarGrpId(Integer tarGrpId) {
        this.tarGrpId = tarGrpId;
    }

    public String getTarGrpName() {
        return tarGrpName;
    }

    public void setTarGrpName(String tarGrpName) {
        this.tarGrpName = tarGrpName;
    }

    public String getTarGrpDesc() {
        return tarGrpDesc;
    }

    public void setTarGrpDesc(String tarGrpDesc) {
        this.tarGrpDesc = tarGrpDesc;
    }

    public String getTarGrpType() {
        return tarGrpType;
    }

    public void setTarGrpType(String tarGrpType) {
        this.tarGrpType = tarGrpType;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
