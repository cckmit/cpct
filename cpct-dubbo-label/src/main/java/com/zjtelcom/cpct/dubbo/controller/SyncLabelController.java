package com.zjtelcom.cpct.dubbo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.LabelServerApplication;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.campaign.OpenCampaignScheEntity;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.dubbo.out.OpenApiScheService;
import com.zjtelcom.cpct.dubbo.out.TarGrpCheckApiService;
import com.zjtelcom.cpct.dubbo.service.SyncEventService;
import com.zjtelcom.cpct.dubbo.service.SyncLabelService;

import com.zjtelcom.cpct.service.campaign.MktCamDirectoryService;
import com.zjtelcom.cpct.service.campaign.OpenCampaignScheService;
import com.zjtelcom.cpct.service.channel.EventRelService;
import com.zjtelcom.cpct.service.report.TopicManagerService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;
import static com.zjtelcom.cpct.service.impl.grouping.ServicePackageServiceImpl.logger;

@RestController
@RequestMapping("/label")
public class SyncLabelController {

    @Autowired
    private SyncLabelService syncLabelService;
    @Autowired
    private SyncEventService syncEventService;
    @Autowired
    private OpenApiScheService openApiScheService;
    @Autowired
    private OpenCampaignScheService openCampaignScheService;
    @Autowired
    private MktCampaignMapper campaignMapper;
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MktCamDirectoryService mktCamDirectoryService;



    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private ObjCatItemRelMapper objCatItemRelMapper;
    @Autowired
    private CatalogItemMapper catalogItemMapper;
    @Autowired
    private ObjectLabelRelMapper objectLabelRelMapper;
    @Autowired
    private TopicLabelMapper topicLabelMapper;
    @Autowired
    private LabelValueMapper labelValueMapper;
    @Autowired
    private TopicManagerService topicManagerService;
    @Autowired
    private TarGrpCheckApiService tarGrpCheckApiService;

    private String gaotao = "2200,1400,1100,3299,3656,1900,9932,8946";
    private String sishengwu = "1600,8444";
    private String huoyue = "1500,2793,2827";
    private String kuandai = "1800";
    private String liuliang = "7544";
    private String qita = "8354,1700,3734,2769,2752,3864,6382,7637,2100,1200,2300";
    private String quanyi = "1300,7967";
    private String ronghe = "3423,6749,6229,3999,8939";

    @Test
    public void aa() {
        String[] positionzj = new String []{"35","36","37"};
        System.out.println(JSON.toJSONString(positionzj));
    }

    //查看所有主题
    @PostMapping("/cpcTarGrpCheck")
    @CrossOrigin
    public Map<String, Object> cpcTarGrpCheck(@RequestBody Map<String,Object> param){

        Map<String,Object> result = new HashMap<>();
        try {
            result = tarGrpCheckApiService.cpcTarGrpCheck(param);
        }catch (Exception e){
            e.printStackTrace();
        }
        return  result;
    }


    //查看所有主题
    @PostMapping("/getTopicList")
    @CrossOrigin
    public Map<String, Object> getTopicList(){

        Map<String,Object> result = new HashMap<>();
        try {
            result = topicManagerService.getAllTopic();
        }catch (Exception e){
            logger.error("[TopicManageController 主题管理  deleteTopic] fail to listEvents for getTopicList = {}! Exception: ", JSONArray.toJSON(""), e);
            e.printStackTrace();
        }
        return  result;
    }



    @PostMapping("/topicLabel")
    @CrossOrigin
    public Map<String, Object> topicLabel(@RequestBody Map<String,Object> param){
        Map<String,Object> result = new HashMap<>();
        try {
            List<MktCampaignDO> allTheme = mktCampaignMapper.getAllTheme("");
            if (!allTheme.isEmpty()) {
                List<List<MktCampaignDO>> lists = ChannelUtil.averageAssign(allTheme, 10);
                for (List<MktCampaignDO> list : lists) {
                    new Thread(){
                        public void run(){
                            for (MktCampaignDO mktCampaignDO : list) {
                                if (mktCampaignDO.getTheMe()==null || mktCampaignDO.getTheMe().length()< 3){
                                    continue;
                                }
                                Long id = 613861144L;
                                if (gaotao.contains(mktCampaignDO.getTheMe())){
                                    id = 613861142L;
                                }
                                if (sishengwu.contains(mktCampaignDO.getTheMe())){
                                    id = 613861136L;
                                }
                                if (huoyue.contains(mktCampaignDO.getTheMe())){
                                    id = 613861143L;
                                }
                                if (kuandai.contains(mktCampaignDO.getTheMe())){
                                    id = 613861138L;
                                }
                                if (liuliang.contains(mktCampaignDO.getTheMe())){
                                    id = 613861137L;
                                }
                                if (qita.contains(mktCampaignDO.getTheMe())){
                                    id = 613861144L;
                                }
                                if (quanyi.contains(mktCampaignDO.getTheMe())){
                                    id = 613861141L;
                                }
                                if (ronghe.contains(mktCampaignDO.getTheMe())){
                                    id = 613861135L;
                                }

                                List<ObjectLabelRel> objectLabelRels = objectLabelRelMapper.selectByObjId(mktCampaignDO.getMktCampaignId());
                                TopicLabelValue topicLabelValue = labelValueMapper.selectByPrimaryKey(id);
                                String  value = "";
                                if (topicLabelValue!=null){
                                    value = topicLabelValue.getLabelValue();
                                }
                                mktCampaignDO.setTheMe(value);
                                mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);
                                if (!mktCampaignDO.getStatusCd().equals("2007") && objectLabelRels.isEmpty()){
                                    ObjectLabelRel aaa = new ObjectLabelRel();
                                    aaa.setObjId(mktCampaignDO.getMktCampaignId());
                                    aaa.setLabelId(613861134L);
                                    aaa.setLabelValue(value);
                                    aaa.setLabelValueId(id);
                                    aaa.setStatusCd("1000");
                                    aaa.setObjType("1900");
                                    aaa.setObjNbr(mktCampaignDO.getMktActivityNbr());
                                    aaa.setCreateDate(new Date());
                                    aaa.setStatusDate(new Date());
                                    aaa.setUpdateDate(new Date());
                                    objectLabelRelMapper.insert(aaa);
                                }
                            }
                        }
                    }.start();
                }

            }
        } catch (Exception e) {
            logger.error("[op:CampaignController] fail to channelEffectDateCheck",e);
        }
        return result;
    }



    @PostMapping("/selectByObjId")
    @CrossOrigin
    public Map<String, Object> selectByObjId(){
        Map<String,Object> result = new HashMap<>();
        try {
            List<MktCampaignDO> allTheme = mktCampaignMapper.getAllTheme("");
            if (!allTheme.isEmpty()) {
                List<List<MktCampaignDO>> lists = ChannelUtil.averageAssign(allTheme, 10);
                for (List<MktCampaignDO> list : lists) {
                    new Thread(){
                        public void run(){
                            for (MktCampaignDO mktCampaignDO : list) {
                                Long aLong = catalogItemMapper.selectCatalogItemIdByCatalogItemDesc(mktCampaignDO.getTheMe());
                                if (aLong==null)
                                {
                                    aLong = 614406331L;
                                }
                                mktCampaignDO.setDirectoryId(aLong);
                                mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);
                                final List<ObjCatItemRel> objCatItemRels = objCatItemRelMapper.selectByObjId(mktCampaignDO.getMktCampaignId());
                                if (!mktCampaignDO.getStatusCd().equals("2007") && objCatItemRels.isEmpty()){
                                    ObjCatItemRel objCatItemRel = new ObjCatItemRel();
                                    objCatItemRel.setObjId(mktCampaignDO.getMktCampaignId());
                                    objCatItemRel.setCatalogItemId(aLong);
                                    objCatItemRel.setStatusCd("1000");
                                    objCatItemRel.setObjType("6000");
                                    objCatItemRel.setObjNbr(mktCampaignDO.getMktActivityNbr());
                                    objCatItemRel.setCreateDate(new Date());
                                    objCatItemRel.setStatusDate(new Date());
                                    objCatItemRel.setUpdateDate(new Date());
                                    objCatItemRelMapper.insert(objCatItemRel);
                                }

                            }
                        }
                    }.start();
                }
            }
        } catch (Exception e) {
            logger.error("[op:CampaignController] fail to channelEffectDateCheck",e);
        }
        return result;
    }



    @RequestMapping(value = "/listAllDirectoryTree", method = RequestMethod.POST)
    @CrossOrigin
    public String listAllDirectoryTree() throws Exception {
        Map<String, Object> directoryMap = mktCamDirectoryService.listAllDirectoryTree();
        return JSON.toJSONString(directoryMap);
    }



    @RequestMapping(value = "openApimktCampaignBorninfoOrder", method = RequestMethod.POST)
    @CrossOrigin
    public String openCampaignScheForDay(@RequestBody HashMap<String,Object> id) {
        Map result = new HashMap();
        MktCampaignDO campaignDO = campaignMapper.selectByPrimaryKey(Long.valueOf(id.get("id").toString()));
        result = openCampaignScheService.openApimktCampaignBorninfoOrder(campaignDO);
//        try {
//            redisUtils.setRedisUnit("TEST_001","123",5);
//            System.out.println(redisUtils.get("TEST_001"));
//            Thread.sleep(7000);
//            System.out.println(redisUtils.get("TEST_001"));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return JSON.toJSON(result).toString();
    }




    @PostMapping("openCampaignSche")
    public  Map<String,Object> openCampaignScheForDay(@RequestBody Map<String,String> param) {
        Map<String,Object> result = new HashMap<>();
        try {
            result = openCampaignScheService.openCampaignScheForDay(Long.valueOf(param.get("campaignId")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    @RequestMapping(value = "openCampaignScheForDay", method = RequestMethod.POST)
    @CrossOrigin
    public String openCampaignScheForDay() {
        Map result = new HashMap();
        result = openApiScheService.openCampaignScheForMonth();
        return JSON.toJSON(result).toString();
    }


    @RequestMapping(value = "syncLabel", method = RequestMethod.POST)
    @CrossOrigin
    public String syncLabel(@RequestBody HashMap<String,Object> model) {
        Map result = new HashMap();

//        result = syncLabelService.initialization();
        result = syncLabelService.syncLabelInfo(model);
        return JSON.toJSON(result).toString();
    }

    @RequestMapping(value = "syncEvent", method = RequestMethod.POST)
    @CrossOrigin
    public String syncEvent(@RequestBody Map<String,Object> param) {
        syncEventService.syncEvent(param);
        return "调用成功";
    }

    @RequestMapping(value = "initLabelCatalog", method = RequestMethod.POST)
    @CrossOrigin
    public String initLabelCatalog() {
        syncLabelService.initLabelCatalog();
        return "调用成功";
    }

    @Test
    public void test() {
        Object newObj = null;
        String  serStr = "%C2%AC%C3%AD%00%05sr%00%0Ejava.lang.Long%3B%C2%8B%C3%A4%C2%90%C3%8C%C2%8F%23%C3%9F%02%00%01J%00%05valuexr%00%10java.lang.Number%C2%86%C2%AC%C2%95%1D%0B%C2%94%C3%A0%C2%8B%02%00%00xp%00%00%00%00%00%00%01%C2%9C";
        try {
            if(serStr != null) {
                String redStr = java.net.URLDecoder.decode(serStr, "UTF-8");
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                newObj = objectInputStream.readObject();
                objectInputStream.close();
                byteArrayInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Autowired
//    private ObjectLabelRelMapper objectLabelRelMapper;
//    @Autowired
//    private CatalogItemMapper catalogItemMapper;
//    @Autowired
//    private ObjCatItemRelMapper objCatItemRelMapper;

    @RequestMapping(value = "addLabelRel", method = RequestMethod.POST)
    @CrossOrigin
    public String addLabelRel(@RequestBody  Map<String,Object> param) {

        List<String> idList = (List<String>) param.get("list");
        String value = param.get("value").toString();
        String valueId = param.get("valueId").toString();
        for (String s : idList) {
            MktCampaignDO campaignDO = campaignMapper.selectByPrimaryKey(Long.valueOf(s));
            ObjectLabelRel a = new ObjectLabelRel();
            a.setLabelId(613861134L);
            a.setLabelCode("100010000008");
            a.setLabelName("营销主题");
            a.setObjNbr(campaignDO.getMktActivityNbr());
            a.setUpdateDate(new Date());
            a.setStatusCd("1000");
            a.setObjType("1900");
            a.setObjId(campaignDO.getMktCampaignId());
            a.setCreateDate(new Date());
            a.setLabelValue(value);
            a.setLabelValueId(Long.valueOf(valueId));
            a.setStatusDate(new Date());
            objectLabelRelMapper.insert(a);
        }
        return "调用成功";
    }

    @PostMapping("aaa")
    @CrossOrigin
    public  void  aaa(MultipartFile file){
        Map<String, Object> result = new HashMap<>();
        try {
            InputStream inputStream = file.getInputStream();
            XSSFWorkbook wb = new XSSFWorkbook(inputStream);
            Sheet sheet = wb.getSheetAt(0);
            Integer rowNums = sheet.getLastRowNum() + 1;

            List<Map<String, Object>> list = new ArrayList<>();
            for (int i = 1; i < rowNums; i++) {
                Map<String, Object> customers = new HashMap<>();
                Row rowCode = sheet.getRow(0);
                Row row = sheet.getRow(i);
                System.out.println("处理--------：" + i);
                if (row == null) {
                    System.out.println("这一行是空的：" + i);
                    continue;
                }
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cellTitle = rowCode.getCell(j);
                    Cell cell = row.getCell(j);
                    customers.put(cellTitle.getStringCellValue(), ChannelUtil.getCellValue(cell));
                }
                list.add(customers);
            }
            for (Map<String, Object> stringObjectMap : list) {
                List<MktCampaignDO> campaignDOS = campaignMapper.listByCode(stringObjectMap.get("营销活动编码").toString());
                if (!campaignDOS.isEmpty()){
                    MktCampaignDO campaignDO = campaignDOS.get(0);
                    CatalogItem catalogItem = catalogItemMapper.selectByCatlogItemCode(stringObjectMap.get("目录编码").toString());
                    if (catalogItem!=null){
                        ObjCatItemRel b = new ObjCatItemRel();
                        b.setCatalogItemId(catalogItem.getCatalogItemId());
                        b.setCatalogItemNbr(catalogItem.getCatalogItemNbr());
                        b.setCatalogItemName(catalogItem.getCatalogItemName());
                        b.setObjNbr(campaignDO.getMktActivityNbr());
                        b.setObjId(campaignDO.getMktCampaignId());
                        b.setCreateDate(new Date());
                        b.setStatusDate(new Date());
                        b.setStatusCd("1000");
                        b.setObjType("6000");
                        objCatItemRelMapper.insert(b);
                    }


//                    ObjectLabelRel a = new ObjectLabelRel();
//                    a.setLabelId(613861134L);
//                    a.setLabelCode("100010000008");
//                    a.setLabelName("营销主题");
//                    a.setObjNbr(campaignDO.getMktActivityNbr());
//                    a.setUpdateDate(new Date());
//                    a.setStatusCd("1000");
//                    a.setObjType("1900");
//                    a.setObjId(campaignDO.getMktCampaignId());
//                    a.setCreateDate(new Date());
//                    a.setLabelValue(stringObjectMap.get("标签值名称").toString());
//                    a.setLabelValueId(Long.valueOf(stringObjectMap.get("标签值编码").toString()));
//                    a.setStatusDate(new Date());
//                    objectLabelRelMapper.insert(a);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }







}
