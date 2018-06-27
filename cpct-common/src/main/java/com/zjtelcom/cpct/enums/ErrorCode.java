package com.zjtelcom.cpct.enums;

/**
 * @Description ErrorCode
 * @Author pengy
 * @Date 2018/6/20 17:55
 */
public enum ErrorCode {

    SEARCH_EVENT_LIST_FAILURE("查询事件列表失败！", "00001"),
    SEARCH_EVENTSORCE_LIST_FAILURE("查询事件源列表失败！", "00002"),
    DELETE_EVENTSORCE_FAILURE("删除事件源失败！","00003"),
    EDIT_EVENTSORCE_FAILURE("编辑事件源失败！","00004"),
    ADD_EVENTSORCE_FAILURE("新增事件源失败！","00005"),
    UPDATE_EVENTSORCE_FAILURE("更新事件源失败！","00006"),
    SEARCH_INTERFACECFG_FAILURE("查询事件源接口列表失败！","00007"),
    SEARCH_EVENTMATCHRUL_FAILURE("查询事件匹配规则列表失败！","00008"),
    ADD_CHANNEL_FAILURE("添加渠道失败","00009"),
    EDIT_CHANNEL_FAILURE("修改渠道失败","00010"),
    DELETE_CHANNEL_FAILURE("删除渠道失败","00011"),
    GET_CHANNEL_LIST("获取渠道列表失败","00012"),
    GET_CHANNEL_DETAIL("获取渠道详情失败","00013"),

    ADD_SCRIPT_FAILURE("添加接触脚本失败","00014"),
    EDIT_SCRIPT_FAILURE("修改接触脚本失败","00015"),
    DELETE_SCRIPT_FAILURE("删除接触脚本失败","00016"),
    GET_SCRIPT_LIST("获取接触脚本列表失败","00017"),
    GET_SCRIPT_DETAIL("获取接触脚本详情失败","00018"),
    ADD_CAM_SCRIPT_FAILURE("添加活动脚本失败","00019"),
    EDIT_CAM_SCRIPT_FAILURE("修改活动脚本失败","000120"),
    DELETE_CAM_SCRIPT_FAILURE("删除活动脚本失败","00021"),
    GET_CAM_SCRIPT_LIST("获取活动脚本列表失败","00022"),
    GET_CAM_SCRIPT_DETAIL("获取活动脚本详情失败","00023"),


    SEARCH_EVENTTYPE_FAILURE("查询事件目录树失败！","00024"),
    SAVE_EVENT_FAILURE("保存事件失败！","00025"),
    SAVE_EVENTTYPE_FAILURE("保存事件类型失败！","00016"),
    EDIT_EVENTTYPE_FAILURE("编辑事件类型失败！","00017"),
    UPDATE_EVENTTYPE_FAILURE("编辑事件类型失败！","00018"),
    DEL_EVENTTYPE_FAILURE("删除事件类型失败！","00019"),
    DEL_EVENT_FAILURE("删除事件失败！","00020"),
    CLOSE_EVENT_FAILURE("关闭事件失败！","00021"),
    EDIT_EVENT_FAILURE("编辑事件失败！","00022"),
    UPDATE_EVENT_FAILURE("更新事件失败！","00022"),
    SEARCH_EVENT_SCENE_LIST_FAILURE("查询事件场景列表失败！", "00023"),
    SAVE_EVENT_SCENE_LIST_FAILURE("新增事件场景列表失败！", "00024"),
    EDIT_EVENT_SCENE_LIST_FAILURE("编辑事件场景列表失败！", "00025"),
    UPDATE_EVENT_SCENE_LIST_FAILURE("更新事件场景列表失败！", "00026"),
    SAVE_TAR_GRP_FAILURE("保存目标分群失败失败！", "00026"),
    DEL_TAR_GRP_CONDITION_FAILURE("删除目标分群条件失败失败！", "00026"),
    EDIT_TAR_GRP_CONDITION_FAILURE("编辑目标分群条件失败失败！", "00026"),
    UPDATE_TAR_GRP_CONDITION_FAILURE("更新目标分群条件失败失败！", "00026"),

    SAVE_MKT_CAMPAIGN_SUCCESS("添加策略配置成功！", "00001"),
    SAVE_MKT_CAMPAIGN_FAILURE("添加策略配置失败！", "00001"),
    UPDATE_MKT_CAMPAIGN_SUCCESS("修改策略配置成功！", "00001"),
    UPDATE_MKT_CAMPAIGN_FAILURE("修改策略配置失败！", "00001"),
    GET_MKT_CAMPAIGN_SUCCESS("修改策略配置失败！", "00001"),
    GET_MKT_CAMPAIGN_FAILURE("修改策略配置失败！", "00001"),
    DELETE_MKT_CAMPAIGN_SUCCESS("删除策略配置成功！", "00001"),
    DELETE_MKT_CAMPAIGN_FAILURE("删除策略配置失败！", "00001");


    private String errorMsg;
    private String errorCode;

    private ErrorCode(final String errorMsg, final String errorCode) {
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
