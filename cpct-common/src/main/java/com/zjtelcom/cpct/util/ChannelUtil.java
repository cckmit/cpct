package com.zjtelcom.cpct.util;

import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.question.Question;
import com.zjtelcom.cpct.domain.question.Questionnaire;
import com.zjtelcom.cpct.dto.campaign.CampaignVO;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.enums.Operator;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

@Component
public class ChannelUtil  {

    public static boolean equalsList(List<Long> list1, List<Long> list2){
        // null情况
        if (list1 == null) {
            return list2 == null;
        }

        // 大小比较
        if (list1.size() != list2.size()) {
            return false;
        }

        Long[] arr1 = list1.toArray(new Long[]{});
        Long[] arr2 = list2.toArray(new Long[]{});
        Arrays.sort(arr1);
        Arrays.sort(arr1);
        return Arrays.equals(arr1,arr2);
    }

    /**
     * 实体类转Map
     * @param sysArea
     * @return
     */
    public static SysArea setOrgArea(SysArea sysArea) {
        String area = sysArea.getAreaId().toString();
        if (area.contains("571")){
            sysArea.setOrgArea("800000000037");
        }
        if (area.contains("570")){
            sysArea.setOrgArea("800000000040");
        }
        if (area.contains("572")){
            sysArea.setOrgArea("800000000021");
        }
        if (area.contains("573")){
            sysArea.setOrgArea("800000000022");
        }
        if (area.contains("574")){
            sysArea.setOrgArea("800000000023");
        }
        if (area.contains("575")){
            sysArea.setOrgArea("800000000024");
        }
        if (area.contains("576")){
            sysArea.setOrgArea("800000000041");
        }
        if (area.contains("577")){
            sysArea.setOrgArea("800000000025");
        }
        if (area.contains("578")){
            sysArea.setOrgArea("800000000039");
        }
        if (area.contains("579")){
            sysArea.setOrgArea("800000000038");
        }
        if (area.contains("580")){
            sysArea.setOrgArea("800000000026");
        }
        return sysArea;
    }


    public static String getOrgByArea(String area) {
        if (area.contains("571")){
            return "800000000037";
        }
        if (area.contains("570")){
            return "800000000040";
        }
        if (area.contains("572")){
            return "800000000021";
        }
        if (area.contains("573")){
            return "800000000022";
        }
        if (area.contains("574")){
            return "800000000023";
        }
        if (area.contains("575")){
            return "800000000024";
        }
        if (area.contains("576")){
            return "800000000041";
        }
        if (area.contains("577")){
            return "800000000025";
        }
        if (area.contains("578")){
            return "800000000039";
        }
        if (area.contains("579")){
            return "800000000038";
        }
        if (area.contains("580")){
            return "800000000026";
        }
        return null;
    }

    public static String getAreaByOrg(String Org) {
        if (Org.contains("800000000037")){
            return "571";
        }
        if (Org.contains("800000000040")){
            return "570";
        }
        if (Org.contains("800000000021")){
            return "572";
        }
        if (Org.contains("800000000022")){
            return "573";
        }
        if (Org.contains("800000000023")){
            return "574";
        }
        if (Org.contains("800000000024")){
            return "575";
        }
        if (Org.contains("800000000041")){
            return "576";
        }
        if (Org.contains("800000000025")){
            return "577";
        }
        if (Org.contains("800000000039")){
            return "578";
        }
        if (Org.contains("800000000038")){
            return "579";
        }
        if (Org.contains("800000000026")){
            return "580";
        }
        return null;
    }

    /**
     * 实体类转Map
     * @param object
     * @return
     */
    public static Map<String, Object> entityToMap(Object object) {
        Map<String, Object> map = new HashMap();
        for (Field field : object.getClass().getDeclaredFields()){
            try {
                boolean flag = field.isAccessible();
                field.setAccessible(true);
                Object o = field.get(object);
                map.put(field.getName(), o);
                field.setAccessible(flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * Map转实体类
     * @param map 需要初始化的数据，key字段必须与实体类的成员名字一样，否则赋值为空
     * @param entity  需要转化成的实体类
     * @return
     */
    public static <T> T mapToEntity(Map<String, Object> map, Class<T> entity) {
        T t = null;
        try {
            t = entity.newInstance();
            for(Field field : entity.getDeclaredFields()) {
                if (map.containsKey(field.getName())) {
                    boolean flag = field.isAccessible();
                    field.setAccessible(true);
                    Object object = map.get(field.getName());
                    if (object!= null && field.getType().isAssignableFrom(object.getClass())) {
                        field.set(t, object);
                    }
                    field.setAccessible(flag);
                }
            }
            return t;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return t;
    }


    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String[] arrayInput(String[] fildList,List<String> codeList){
        List<String> resultList = new ArrayList<>();
        for (String code : fildList){
            resultList.add(code);
        }
        for (String code : codeList){
            if (resultList.contains(code)){
                continue;
            }
            resultList.add(code);
        }
        String[] fieldList = new String[resultList.size()];
        for (int i = 0; i < resultList.size(); i++) {
            fieldList[i] = resultList.get(i);
        }
        return fieldList;
    }

    /**
     * 将一个list均分成n个list,主要通过偏移量来实现的
     * @param source
     * @return
     */
    public static <T> List<List<T>> averageAssign(List<T> source,int n) {
        List<List<T>> result = new ArrayList<List<T>>();
        int remaider = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = new ArrayList<T>(source.subList(i * number + offset, (i + 1) * number + offset + 1));
                remaider--;
                offset++;
            } else {
                value =  new ArrayList<T>(source.subList(i * number + offset, (i + 1) * number + offset));
            }
            result.add(value);
        }
        return result;
    }


    public static String getQuestionType(Question question){
        String st = "";
        if (question.getQuestionType()==null || question.getQuestionType().equals("")){
            return st;
        }
        switch (question.getQuestionType()){
            case "1000":
                st = "单选题";
                break;
            case "2000":
                st = "多选题";
                break;
        }
        return st;
    }

    public static String getNaireType(Questionnaire questionnaire){
        String st = "";
        if (questionnaire.getNaireType()==null || questionnaire.getNaireType().equals("")){
            return st;
        }
        switch (questionnaire.getNaireType()){
            case "1000":
                st = "营销问卷";
                break;
            case "2000":
                st = "维挽问卷";
                break;
        }
        return st;
    }

    public static String getAnswerType(Question question){
        //1000	日期输入框;2000	下拉选择框;3000	文本输入框;4000	单选框;5000	字符编辑框;6000	是与否控制框;7000	数值输入框;8000	多选框;9000	文本标签
        String st = "";
        switch (question.getAnswerType()){
            case "1000":
                st = "日期输入框";
                break;
            case "2000":
                st = "下拉选择框";
                break;
            case "3000":
                st = "文本输入框";
                break;
            case "4000":
                st = "单选框";
                break;
            case "5000":
                st = "字符编辑框";
                break;
            case "6000":
                st = "是与否控制框";
                break;
            case "7000":
                st = "数值输入框";
                break;
            case "8000":
                st = "多选框";
                break;
            case "9000":
                st = "文本标签";
                break;
        }
        return st;
    }



    public static String getUUID(){
        UUID uuid=UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr=str.replace("-", "");
        return uuidStr;
    }
    public static CampaignVO map2CampaignVO(MktCampaignDO campaignDO){
        CampaignVO vo = BeanUtil.create(campaignDO,new CampaignVO());
        vo.setMktCampaignId(campaignDO.getMktCampaignId());
        vo.setCampaignName(campaignDO.getMktCampaignName());
        return vo;
    }

    public static String StringList2String(List<String> stringList){
        if (stringList == null || stringList.isEmpty()){
            return "";
        }
        String[] sts = new String[stringList.size()];
        for (int i = 0;i<sts.length;i++){
            sts[i] = stringList.get(i);
        }
        return StringUtils.join(sts,",");
    }

    public static String list2String(List<String> stringList,String separator){
        if (stringList == null || stringList.isEmpty()){
            return "";
        }
        String[] sts = new String[stringList.size()];
        for (int i = 0;i<sts.length;i++){
            sts[i] = stringList.get(i);
        }
        return StringUtils.join(sts,separator);
    }

    public static String valueVOList2String(List<LabelValueVO> valueVOList){
        if (valueVOList == null || valueVOList.isEmpty()){
            return "";
        }
        String[] sts = new String[valueVOList.size()];
        for (int i = 0;i<sts.length;i++){
            sts[i] = valueVOList.get(i).getValueName();
        }
        return StringUtils.join(sts,"/");
    }


    public static String idList2String(List<Long> idList,String separator){
        if (idList == null || idList.isEmpty()){
            return "";
        }
        Long[] ids = new Long[idList.size()];
        for (int i = 0;i<ids.length;i++){
            ids[i] = idList.get(i);
        }
        return StringUtils.join(ids,separator);
    }

    public static String idList2String(List<Long> idList){
        if (idList == null || idList.isEmpty()){
            return "";
        }
        Long[] ids = new Long[idList.size()];
        for (int i = 0;i<ids.length;i++){
            ids[i] = idList.get(i);
        }
        return StringUtils.join(ids,",");
    }

    public static String List2String(List<Integer> idList){
        if (idList == null || idList.isEmpty()){
            return "";
        }
        Integer[] ids = new Integer[idList.size()];
        for (int i = 0;i<ids.length;i++){
            ids[i] = idList.get(i);
        }
        return StringUtils.join(ids,",");
    }

    public static ChannelVO map2ChannelVO(Channel channel){
        ChannelVO vo = BeanUtil.create(channel,new ChannelVO());
        return vo;
    }

    public static ScriptVO map2ScriptVO(Script script){
        ScriptVO vo = BeanUtil.create(script,new ScriptVO());
        return vo;
    }

    public static CamScriptVO map2CamScriptVO(CamScript script){
        CamScriptVO vo = BeanUtil.create(script,new CamScriptVO());
        return vo;
    }

    public static LabelVO map2LabelVO(Label label,List<LabelValue> labelValueList){
        LabelVO vo = BeanUtil.create(label,new LabelVO());
        vo.setLabTechDesc(label.getLabExample());
        if (label.getOperator()!=null && !label.getOperator().equals("")){
            List<String> opratorList = StringToList(label.getOperator());
            List<OperatorDetail> opStList  = new ArrayList<>();
            for (String operator : opratorList){
                Operator op = Operator.getOperator(Integer.valueOf(operator));
                OperatorDetail detail = new OperatorDetail();
                if (op!=null){
                    detail.setOperName(op.getDescription());
                    detail.setOperValue(op.getValue());
                }
                opStList.add(detail);
            }
            vo.setOperatorList(opStList);
        }
        List<LabelValueVO> valueVOList = new ArrayList<>();
        if (labelValueList!=null && !labelValueList.isEmpty()){
            valueVOList = valueList2VOList(labelValueList);
            vo.setLabelValueSt(valueVOList2String(valueVOList));
        }
        if (label.getScope().equals(0)){
            vo.setScope("自有标签");
        }else {
            vo.setScope("大数据标签");
        }
        vo.setValueList(valueVOList);
        return vo;
    }

    public static LabelValueVO map2LabelValueVO(LabelValue labelValue){
        LabelValueVO vo = BeanUtil.create(labelValue,new LabelValueVO());
        return vo;
    }

    public static VerbalVO map2VerbalVO(MktVerbal verbal){
        VerbalVO vo = BeanUtil.create(verbal,new VerbalVO());
        return vo;
    }

    public static String getRandomStr(int length) {
        char[] chars = "23456789".toCharArray();
        Random r = new Random(System.currentTimeMillis());
        String string = "";

        for(int i = 0; i < length; ++i) {
            string = string + chars[r.nextInt(8)];
        }

        return string;
    }


    public static List<String> valueList2StList(List<LabelValue> valueList){
        List<String> stringList = new ArrayList<>();
        for (LabelValue labelValue : valueList){
            if (labelValue.getLabelValue()!=null){
                stringList.add(labelValue.getValueName());
            }
        }
        return stringList;
    }


    public static List<LabelValueVO> valueList2VOList(List<LabelValue> valueList){
        List<LabelValueVO> stringList = new ArrayList<>();
        for (LabelValue labelValue : valueList){
            LabelValueVO vo = BeanUtil.create(labelValue,new LabelValueVO());
//            vo.setLabelValue(labelValue.getValueName());
            stringList.add(vo);
        }
        return stringList;
    }



    public static List<String> StringToList(String var1) {
        String[] array = var1.split(",");
        List<String> list = new ArrayList<String>();
        for (String str : array)
        {
            list.add(str);
        }
        return list;
    }

    public static List<String> StringToList4LabelValue(String var1) {
        String[] array = var1.split("/");
        List<String> list = new ArrayList<String>();
        for (String str : array)
        {
            list.add(str);
        }
        return list;
    }

    public static Object getCellValue(Cell cell) {
        Object cellValue;
        if (cell==null){
            return "null";
        }
        switch (cell.getCellTypeEnum()){
            case NUMERIC://数字
                cell.setCellType(CellType.STRING);
                cellValue = cell.getStringCellValue().equals("") ? "null" : cell.getStringCellValue()+"";
                break;
            case STRING: // 字符串
                cellValue = cell.getStringCellValue().equals("") ? "null" : cell.getStringCellValue()+"";
                break;
            case BOOLEAN: // Boolean
                cellValue = cell.getBooleanCellValue() + "";
                break;
            case FORMULA: // 公式
                cellValue = cell.getCellFormula() + "";
                break;
            case BLANK: // 空值
                cellValue = "null";
                break;
            case ERROR: // 故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }

    public static List<Long> StringToidList(String var1) {
        String[] array = var1.split(",");
        List<Long> list = new ArrayList<Long>();
        for (String str : array)
        {
            list.add(Long.valueOf(str));
        }
        return list;
    }
    public static List<Long> StringToIdList(String var1) {
        String[] array = var1.split("/");
        List<Long> list = new ArrayList<Long>();
        for (String str : array)
        {
            list.add(Long.valueOf(str));
        }
        return list;
    }

    public static String getDataType(String dataType){
        //1000	日期型;1100	日期时间型;1200	字符型;1300	浮点型;1400	整数型;1500	布尔型;1600	计算型
        String data = "";
        switch (dataType){
            case "text":
                data = "1200";
                break;
            case "number":
                data = "1400";
                break;
            case "date":
                data = "1100";
                break;
            case "integer":
                data = "1400";
                break;
            case "character":
                data = "1200";
                break;
            case "smallint":
                data = "1600";
                break;
        }
        return data;
    }



}
