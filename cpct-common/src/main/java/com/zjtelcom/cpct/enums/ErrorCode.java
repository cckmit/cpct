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
    SEARCH_EVENTTYPE_FAILURE("查询事件目录树失败！","00009"),
    SAVE_EVENT_FAILURE("保存事件失败！","00010"),
    ADD_CHANNEL_FAILURE("添加渠道失败","00011"),
    EDIT_CHANNEL_FAILURE("修改渠道失败","00012"),
    DELETE_CHANNEL_FAILURE("删除渠道失败","00013"),
    GET_CHANNEL_LIST("获取渠道列表失败","00014"),
    GET_CHANNEL_DETAIL("获取渠道详情失败","00015"),
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
    UPDATE_EVENT_SCENE_LIST_FAILURE("更新事件场景列表失败！", "00026");

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
