package com.zjtelcom.cpct.constants;

/**
 * 
 * <p>统一返回码定义</p>
 * 编码规则：
 * 中心标识+异常类型（2位数字）+序号（4位数字）</br>
 * <table>
        <thead>
            <tr>
                <th>编码</th>
                <th>描述</th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td>00</td>
                <td>未知异常</td>
            </tr>
            <tr>
                <td>01</td>
                <td>数据库连接异常</td>
            </tr>
            <tr>
                <td>02</td>
                <td>数据库操作异常</td>
            </tr>
            <tr>
                <td>03</td>
                <td>参数异常</td>
            </tr>
            <tr>
                <td>04</td>
                <td>HTTP异常</td>
            </tr>
            <tr>
                <td>05</td>
                <td>服务异常</td>
            </tr>
            <tr>
                <td>06</td>
                <td>业务异常</td>
            </tr>
        </tbody>
    </table>
 * @author taowenwu
 * @version 1.0
 * @see ResponseCode
 * @since
 */
public interface ResponseCode {
    String SUCCESS = "0";

    String SUCCESS_MSG = "处理成功";

    String FAIL_MSG = "处理失败";

    String CANNOT_QUERY_RULE = "600104050001";

    String CANNOT_QUERY_RULE_MSG = "canot query rule";

    String CALCULATE_FAILED = "600104050002";

    String CALCULATE_FAILED_MSG = "calculate failed";

    String INTERNAL_ERROR = "600104000001";

    String INTERNAL_ERROR_MSG = "未知错误，请联系接口人员！";

    String SERVICE_ERROR = "600104050001";

    String SERVICE_ERROR_MSG = "服务调用错误，请联系接口人员！";

    String VALIDATE_ERROR = "600104030001";

    String VALIDATE_ERROR_MSG = "参数校验出错，请联系接口人员！";

    String DATABASE_ERROR = "600104010001";

    String DATABASE_ERROR_MSG = "数据操作错误，请联系接口人员！";

    String POLICY_TRY_ERROR = "600104060001";

    String CHANNEL_REPEAT_ERROR = "600104060002";

    String CHANNEL_REPEAT_ERROR_MSG = "渠道编码不允许重复！";

    String SOCKET_ERROR_MSG = "网络连接错误，请联系接口人员！";

    String SOCKET_ERROR = "600104070001";

}
