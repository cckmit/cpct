package com.zjtelcom.cpct.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.common.CacheConstants;
import com.zjtelcom.cpct.common.CacheManager;
import com.zjtelcom.cpct.common.IDacher;
import com.zjtelcom.cpct.constants.ConditionTypeConstants;
import com.zjtelcom.cpct.constants.UseTypeConstants;
import com.zjtelcom.cpct.dao.eagle.EagleSourceTableDefMapper;
import com.zjtelcom.cpct.dao.eagle.EagleSourceTableRefMapper;
import com.zjtelcom.cpct.dto.system.SystemParam;
import com.zjtelcom.cpct.model.*;
import com.zjtelcom.cpct.pojo.Company;
import com.zjtelcom.cpct.service.EagleDatabaseConfCache;
import com.zjtelcom.cpct.strategyengine.Combination;
import freemarker.template.*;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.context.ContextLoader;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * 组装针对性营销需要的sql工具类
 *
 * @author taowenwu
 * @version 1.0
 * @see SqlUtil
 * @since JDK1.7
 */
public final class SqlUtil {

    private static final Logger LOG = Logger.getLogger(SqlUtil.class);

    private static final String MAIN_FLAG = "1";

    private static boolean initFlag;

    private static EagleSourceTableRefMapper sourceTableRefMapper;

    private static EagleSourceTableDefMapper sourceTableDefMapper;

//    private static TriggerMapper triggerMapper;

    private static Configuration config;

    private static Template template;

    private SqlUtil() {
    }

    /**
     * 组装sql
     *
     * @param domainType  所属域
     * @param triggers    因子集合
     * @param tagInfos    出参，针对性标签集合
     * @param company     组织单位
     * @param tagInfoKeys 标签keyMap，防止重复标签
     * @return 根据因子组装后的sql
     * @see
     */
    @SuppressWarnings("unchecked")
    public static String integrationSql(String domainType, List<Map<String, String>> triggers,
                                        List<Map<String, String>> tagInfos, List<Company> company,
                                        Map<String, String> tagInfoKeys) {
        init();

        //防止，组装sql中出现重复列，重复表关联，用Map区分是否有重复数据
        Map<String, EagleSourceTableRef> keys = new HashMap<>(10);

        IDacher<EagleTag> tagCache = CacheManager.getInstance().getCache(
                CacheConstants.TAG_CACHE_NAME);
        IDacher<LabelValue> triggerValueCache = CacheManager.getInstance().getCache(
                CacheConstants.TRIGGER_VALUE_CACHE_NAME);
        IDacher<Label> triggerCache = CacheManager.getInstance().getCache(
                CacheConstants.TRIGGER_CACHE_NAME);
        IDacher<EagleTagAdaption> tagAdaptionCache = CacheManager.getInstance().getCache(
                CacheConstants.TAG_ADAPTION_CACHE_NAME);

        EagleTagAdaption eagleTagAdaption = tagAdaptionCache.queryOne("EAGLE_ALL_TAG_1_TRYCALC");

        Set<EagleSourceTableRef> tables = new HashSet<>();
        List<Column> columns = new ArrayList<>();

        //查询标签对应的valueid
        //todo 调用侯云峰
//        List<Trigger> triggerList = triggerMapper.queryTriggerByLeftOpers(triggers);
        List<Label> triggerList = new ArrayList<>();
        Map<String, Label> triggerMap = new HashMap<>(triggerList.size());
        for (Label trigger : triggerList) {
            triggerMap.put(trigger.getConditionId().toString(), trigger);
        }

        EagleSourceTableDef masterTableDef = getMasterTable(domainType);
        //主表别名
        masterTableDef.setAlias("t0");

        LOG.debug(String.format("masterTableDef: %s", JSON.toJSONString(masterTableDef)));

        List<Map<String, String>> tagList = new ArrayList<>();

        int k = 1;
        for (Map tigger : triggers) {

            String conditionId = tigger.get("conditionId").toString();

            String leftOper = triggerCache.queryOne(conditionId).getEagleName();
            leftOper = (String) GroovyUtil.invokeMethod(eagleTagAdaption.getScript(), "process",
                    new Object[]{tigger, domainType});
            String key = leftOper + "_" + domainType;
            EagleTag tag = tagCache.queryOne(key);
            //表别名
            String as = "t" + k;

            if (null != tag) {

                // 数据类型
                tigger.put("dataType", tag.getSourceTableColumnType());

                EagleSourceTableDef tableDef = (EagleSourceTableDef) CacheManager.getInstance().getCache(
                        CacheConstants.SOURCE_TABLE_DEF_CACHE_NAME).queryOne(
                        tag.getCtasTableDefinitionRowId().toString());

                // 查询出跟主表的关联表
                EagleSourceTableRef joinTable = getJoinTable(masterTableDef, tableDef,
                        tag.getFitDomain(), as);

                String joinTableKey = null;
                if (null != tableDef) {
                    joinTableKey = new StringBuilder(
                            tableDef.getCtasTableDefinitionRowId().toString()).append("-").append(
                            masterTableDef.getCtasTableDefinitionRowId().toString()).append("-").append(
                            domainType).toString();
                }

                if (null != tableDef && null != joinTable) {

                    //重复数据就不再添加
                    if (!keys.containsKey(joinTableKey)) {
                        tables.add(joinTable);
                        keys.put(joinTableKey, joinTable);
                    }
                } else {
                    LOG.error("tableDefId: " + tag.getCtasTableDefinitionRowId() + " cannot query");
                }

                String useType = tigger.get("useType").toString();
                useType = useType == null ? UseTypeConstants.CONDITION_RESULT : useType;

                if (null == joinTable) {
                    as = "t0";
                } else {
                    as = keys.get(joinTableKey).getAlias();
                }

                Column col = new Column(leftOper, as);
                if (null != tableDef
                        && !keys.containsKey(tableDef.getCtasTableDefinitionRowId() + "-" + leftOper)) {
                    // 只有3和2才能在select中出现
                    if (UseTypeConstants.CONDITION_RESULT.equals(useType)
                            || UseTypeConstants.RESULT.equals(useType)) {
                        columns.add(col);
                    }

                    keys.put(tableDef.getCtasTableDefinitionRowId() + "-" + leftOper, null);
                }
                if (null != tagInfos) {
                    addTagInfo(tagInfos, tag, tagInfoKeys);
                }
                tigger.put("leftOperand", col.getTableAlias() + "." + col.getName());

                if (triggerMap.containsKey(conditionId.toString())) {
                    Integer valueId = triggerMap.get(conditionId.toString()).getValueId();
                    String conditionType = tigger.get("conditionType").toString();

                    //类型是输入和范围的不用查询真实值
                    if (null != valueId) {
                        String rightOperand = tigger.get("rightOperand").toString();
                        StringBuilder realValue = new StringBuilder();

                        //组合情况下需要特殊处理
                        if (ConditionTypeConstants.ASSEMBLE.equals(conditionType)) {
                            rightOperand = formatRightOperand(rightOperand);
                        }
                        String[] showValues = rightOperand.split(",");
                        for (int i = 0; i < showValues.length; i++) {

                            String triggerValueCacheKey = new StringBuilder(valueId.toString()).append(
                                    "@").append(domainType).append("@").append(showValues[i]).toString();
                            LabelValue triggerValue = triggerValueCache.queryOne(triggerValueCacheKey);
                            if (null != triggerValue) {
                                realValue.append(triggerValue.getRealValue());
                            } else {
                                realValue.append(showValues[i]);
                            }
                            realValue.append(",");
                        }

                        if (realValue.length() != 0) {
                            realValue = realValue.delete(realValue.length() - 1,
                                    realValue.length());
                            tigger.put("rightOperand", realValue.toString());
                        }
                    }
                }
                tagList.add(tigger);
            } else {
                LOG.error("get cache tag null, key: " + key);
            }
            k++;
        }

        // 必选字段要出现在sql中
        SystemParam param = (SystemParam) CacheManager.getInstance().getCache(
                CacheConstants.SYSTEMPARAM_CACHE_NAME).queryOne("eagle.necessary.tag");
        String[] requiredTags = param.getParamValue().split(",");

        //k = 1;
        for (String requiredTag : requiredTags) {
            String as = "t" + k;
            String key = requiredTag + "_" + domainType;
            String tagAdaptionCacheKey = key + "_TRYCALC";

            EagleTagAdaption tagAdaption = tagAdaptionCache.queryOne(tagAdaptionCacheKey);
            // ITV域的本地网需要特殊处理
            if (null != tagAdaption) {
                key = (String) GroovyUtil.invokeMethod(tagAdaption.getScript(), "process",
                        new Object[]{requiredTag, domainType});
            }

            EagleTag tag = tagCache.queryOne(key);
            requiredTag = tag.getSourceTableColumnName();
            if (null != tag) {
                EagleSourceTableDef tableDef = (EagleSourceTableDef) CacheManager.getInstance().getCache(
                        CacheConstants.SOURCE_TABLE_DEF_CACHE_NAME).queryOne(
                        tag.getCtasTableDefinitionRowId().toString());

                EagleSourceTableRef joinTable = getJoinTable(masterTableDef, tableDef,
                        tag.getFitDomain(), as);

                String joinTableKey = null;
                if (null != tableDef) {
                    joinTableKey = new StringBuilder(
                            tableDef.getCtasTableDefinitionRowId().toString()).append("-").append(
                            masterTableDef.getCtasTableDefinitionRowId().toString()).append("-").append(
                            domainType).toString();
                }

                if (null != tableDef && null != joinTable) {
                    if (!keys.containsKey(joinTableKey)) {
                        tables.add(joinTable);
                        keys.put(joinTableKey, joinTable);
                    }
                } else {
                    LOG.error("tableDefId: " + tag.getCtasTableDefinitionRowId() + " cannot query");
                }

                if (null == joinTable) {
                    as = "t0";
                } else {
                    as = keys.get(joinTableKey).getAlias();
                }

                Column col = new Column(requiredTag, as);
                if (null != tableDef
                        && !keys.containsKey(tableDef.getCtasTableDefinitionRowId() + "-"
                        + tag.getSourceTableColumnName())) {
                    columns.add(col);
                    keys.put(
                            tableDef.getCtasTableDefinitionRowId() + "-"
                                    + tag.getSourceTableColumnName(), null);
                }
                if (null != tagInfos) {
                    addTagInfo(tagInfos, tag, tagInfoKeys);
                }
            } else {
                LOG.error("get necessary tag null, key: " + key);
            }
            k++;
        }

        Map<String, Object> dataModel = new HashMap<>(4);
        Collections.sort(columns);
        dataModel.put("columns", columns);
        dataModel.put("masterTable", masterTableDef);
        dataModel.put("tables", tables);
        dataModel.put("triggers", tagList);
        dataModel.put("company", company);
        StringWriter out = new StringWriter();
        try {
            template.process(dataModel, out);
        } catch (Exception e) {
            LOG.error(e);
        }
        return out.toString();
    }

    private static String formatRightOperand(Object rightOperand) {
        if (null != rightOperand && !"".equals(rightOperand.toString())) {
            JSONArray rightOperands = JSONArray.parseArray(rightOperand.toString());
            if (null != rightOperands) {
                List<String> source = new ArrayList<String>();
                List<String> keys = new ArrayList<String>();
                for (int i = 0; i < rightOperands.size(); i++) {
                    JSONObject object = rightOperands.getJSONObject(i);
                    List<String> listKeys = new ArrayList<String>(object.keySet());
                    if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listKeys)) {
                        String key = listKeys.get(0);
                        if (!"".equals(key)) {
                            Integer value = MapUtils.getInteger(object, key, 0);
                            if (1 == value.intValue()) {
                                keys.add(key);
                            }
                            source.add(key);
                        }
                    }
                }
                List<String> rightOperandList = Combination.combiantion(source, keys);
                String rightOperandStr = com.zjtelcom.cpct.util.CollectionUtils.list2String(rightOperandList);
                return rightOperandStr;
            }
        }
        return "";
    }

    public static EagleSourceTableDef getMasterTable(String domainType) {
        init();
        // 查询出这个域下主表
        SystemParam param = (SystemParam) CacheManager.getInstance().getCache(
                CacheConstants.SYSTEMPARAM_CACHE_NAME).queryOne("eagle.master.table");
        JSONObject obj = JSON.parseObject(param.getParamValue());
        String materTable = obj.getString(domainType);
        EagleDatabaseConfig config = (EagleDatabaseConfig) CacheManager.getInstance().getCache(
                CacheConstants.DATABASE_COPNFIG_CACHE_NAME).queryOne(
                EagleDatabaseConfCache.CACHE_DB2_KEY);

        EagleSourceTableDef masterTableDef = sourceTableDefMapper.queryByTableNameAndDb(
                materTable, config.getDbConfRowId().toString());
        return masterTableDef;
    }

    private static void addTagInfo(List<Map<String, String>> tagInfos, EagleTag tag,
                                   Map<String, String> keys) {
        if (!keys.containsKey(tag.getTagRowId().toString())) {
            Map<String, String> tagInfo = new HashMap<>(3);
            tagInfo.put("tagId", tag.getTagRowId().toString());
            tagInfo.put("tagName", tag.getTagName());
            tagInfo.put("tagCode", tag.getSourceTableColumnName());
            tagInfos.add(tagInfo);
            keys.put(tag.getTagRowId().toString(), null);
        }
    }

    private static EagleSourceTableRef getJoinTable(EagleSourceTableDef masterTableDef,
                                                    EagleSourceTableDef tableDef,
                                                    String fitDomain, String as) {
        // 非主表
        if (null != tableDef && !MAIN_FLAG.equals(tableDef.getTagTableMainFlag())) {
            // 查询
            List<EagleSourceTableRef> sourceTableRefList = sourceTableRefMapper.queryByMainFlagAndFitDomain(
                    tableDef.getCtasTableDefinitionRowId().toString(),
                    masterTableDef.getCtasTableDefinitionRowId().toString(), fitDomain);
            EagleSourceTableRef tableRef = null;
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(sourceTableRefList)) {
                tableRef = sourceTableRefList.get(0);
                tableRef.setTableName(tableDef.getTagTableNameEn());
                tableRef.setAlias(as);
                tableRef.setSchemaName(tableDef.getSchemaName());
            }
            return tableRef;
        }

        return null;
    }

    private static void init() {
        if (!initFlag) {
//            sourceTableRefMapper = ContextLoader.getCurrentWebApplicationContext().getBean(
//                EagleSourceTableRefMapper.class);
//            sourceTableDefMapper = ContextLoader.getCurrentWebApplicationContext().getBean(
//                EagleSourceTableDefMapper.class);
//            triggerMapper = ContextLoader.getCurrentWebApplicationContext().getBean(
//                TriggerMapper.class);

            config = new Configuration(new Version("2.3.23"));
            config.setDefaultEncoding("UTF-8");

            Map<String, Object> methods = new HashMap<>(1);
            // 添加定义函数
            methods.put("resolveCond", new TemplateTriggerFunction());
            try {
                config.setAllSharedVariables(new SimpleHash(methods, config.getObjectWrapper()));
            } catch (TemplateModelException e1) {
                LOG.error(e1);
            }

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                    ThisOrTargetAnnotationPointcut.class.getClassLoader());
            // 获取ftl文件
            Resource resource = resolver.getResource("classpath:config/trycalc.ftl");
            try {
                String ftl = FileUtils.readFileToString(resource.getFile(), "UTF-8");
                // 创建模板对象
                template = new Template(ftl, new StringReader(ftl), config);
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }
}
