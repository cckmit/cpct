package com.zjtelcom.cpct.util;


import com.zjtelcom.cpct.common.CacheConstants;
import com.zjtelcom.cpct.common.CacheManager;
import com.zjtelcom.cpct.dto.system.SystemParam;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;


/**
 * 
 * freemaker模板内置方法，用于组装sql的where条件
 * @author taowenwu
 * @version 1.0
 * @see TemplateTriggerFunction
 * @since JDK1.7
 */
public class TemplateTriggerFunction implements TemplateMethodModelEx {
    private static final Logger LOG = Logger.getLogger(TemplateTriggerFunction.class);

    @Override
    public Object exec(List args)
        throws TemplateModelException {

        try {
            String col = args.get(0).toString();
            String conditionType = args.get(1).toString();
            String operator = args.get(2).toString();
            String value = args.get(3).toString();
            String dataType = args.get(4).toString();

            return getSqlCondition(conditionType, col, operator, value, dataType);
        }
        catch (Exception e) {
            LOG.error(e);
            throw new TemplateModelException(e);
        }
    }

    private String getSqlCondition(String conditionType, String col, String operator,
                                   String value, String dataType) {
        String condition = null;

        // 这里还需要将value对应的真实值查询处理，待接口确认后开发

        if (ConditionTypeConstants.SINGLE_SELECT.equals(conditionType)
            || ConditionTypeConstants.MULTIPLE_SELECT.equals(conditionType)
            || ConditionTypeConstants.INPUT.equals(conditionType)) {
            condition = getSqlOperator(operator, value, dataType, col);
        }
        else if (ConditionTypeConstants.RAGNE.equals(conditionType)) {
            String[] values = value.split(",");

            condition = new StringBuilder("( ").append(col).append(">=").append(
                formatValue(values[0], dataType, operator)).append(" and ").append(col).append(" ").append(
                "<=").append(formatValue(values[1], dataType, operator)).append(" )").toString();

        }
        else {
            condition = getSqlOperator(operator, value, dataType, col);
        }

        return condition;
    }

    private String getSqlOperator(String operator, String value, String dataType, String col) {
        String compare = null;
        String operatorType = operator;
        String[] colName = col.split("\\.");
        //特殊处理
        if (colName[1].equals("TYPE_4G_FLG")) {
            operatorType = OperatorConstants.IN;
        }
        switch (operatorType) {
            case OperatorConstants.LESS_THAN:
                compare = col + "<" + formatValue(value, dataType, operatorType);
                break;
            case OperatorConstants.GREATER_THAN_EQUAL:
                compare = col + "<=" + formatValue(value, dataType, operatorType);
                break;
            case OperatorConstants.EQUAL:
                compare = col + "=" + formatValue(value, dataType, operatorType);
                break;
            case OperatorConstants.NOT_EQUAL:
                compare = col + "<>" + formatValue(value, dataType, operatorType);
                break;
            case OperatorConstants.GREATER_THAN:
                compare = col + ">" + formatValue(value, dataType, operatorType);
                break;
            case OperatorConstants.LESS_THAN_EQUAL:
                compare = col + ">=" + formatValue(value, dataType, operatorType);
                break;
            case OperatorConstants.NOT_IN:
                compare = col + " not in";
                break;
            case OperatorConstants.IN:
                compare = col + " in";
                break;
            default:
                compare = col + "=" + formatValue(value, dataType, operatorType);
                break;
        }

        if (OperatorConstants.NOT_IN.equals(operatorType)
            || OperatorConstants.IN.equals(operatorType)) {
            String[] values = value.split(",");
            StringBuilder valueBf = new StringBuilder();
            for (int i = 0; i < values.length; i++ ) {
                valueBf.append(formatValue(values[i], dataType, operatorType)).append(",");
            }

            // 去掉最后一个逗号
            if (null != valueBf.toString()) {
                String valueBfStr = valueBf.substring(0, valueBf.length() - 1);
                compare = new StringBuilder(compare).append("(").append(valueBfStr).append(")").toString();
            }

        }

        return compare;
    }

    private String formatValue(String value, String dataType, String operator) {
        SystemParam param = (SystemParam) CacheManager.getInstance().getCache(
            CacheConstants.SYSTEMPARAM_CACHE_NAME).queryOne("cpc.eagle.datatype");
        if (value.contains(" ") && (OperatorConstants.IN.equals(operator)
            || OperatorConstants.NOT_IN.equals(operator))) {

            String[] values = value.split(" ");
            StringBuilder builder = new StringBuilder();
            for (String v : values) {
                builder.append(formatOnly(v, dataType, param)).append(",");
            }

            if (StringUtils.isNotEmpty(builder.toString())) {
                return builder.substring(0, builder.length() - 1);
            }

        }
        return formatOnly(value, dataType, param);
    }

    private String formatOnly(String value, String dataType, SystemParam param) {
        String[] dataTypes = param.getParamValue().split(",");
        for (String type : dataTypes) {
            if (type.equals(dataType)) {
                return "'" + value + "'";
            }
        }
        return value;
    }

}
