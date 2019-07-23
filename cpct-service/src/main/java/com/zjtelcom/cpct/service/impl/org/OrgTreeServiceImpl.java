package com.zjtelcom.cpct.service.impl.org;

import com.ctzj.smt.bss.centralized.web.util.BssSessionHelp;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.fasterxml.jackson.databind.node.LongNode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.OrganizationMapper;
import com.zjtelcom.cpct.dao.org.OrgTreeMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.channel.Organization;
import com.zjtelcom.cpct.domain.org.OrgTree;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.enums.ORG2RegionId;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.org.OrgTreeService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.FtpUtils;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: anson
 * @Date: 2018/9/30
 * @Description:营销组织树
 */
@Service
@Transactional
public class OrgTreeServiceImpl implements OrgTreeService{

    @Autowired
    private OrgTreeMapper orgTreeMapper;
    @Autowired
    private SysParamsMapper systemParamMapper;
    @Autowired
    private OrganizationMapper organizationMapper;

    /**
     * 批量操作一次插入最多的数据条数
     */
    private static final int max=10000;


    /**
     * 按条件查找
     * @param t
     * @return
     */
    @Override
    public List<OrgTree> queryList(OrgTree t) {
        return orgTreeMapper.queryList(t);
    }




    /**
     * 批量添加数据
     * @param list
     * @return
     */
    @Override
    public int addBatchData(List<OrgTree> list) {
        return orgTreeMapper.addBatch(list);
    }

    /**
     * 从本地取dat文件的数据   注意文件的编码
     * @param path
     * @return
     */
    @Override
    public List<OrgTree> getDataByFtp(String path) {
        String code="utf8";
//        List<SysParams> paramKeyIn = systemParamMapper.findParamKeyIn(path);
//        if(!paramKeyIn.isEmpty()){
//            code=paramKeyIn.get(0).getParamValue();
//        }
        List<OrgTree> list = changeToList(path, code);
//        long start=System.currentTimeMillis();
//        //先删除该表所有数据
//        orgTreeMapper.deleteAll();
//        System.out.println("删除所有数据完毕");
//        System.out.println("开始插入数据");
//        //  sql上限  4194304   目前数据31008888(117530条数据,每条数据sql长度270)  计算一次插入1W条
//        batchToInsert(list);
//        System.out.println("数据插入完毕 耗时："+(System.currentTimeMillis()-start));

        return list;
    }


    /**
     *
     * @param remoteFileName  需要下载的文件名
     */
    public  void getOrgTreeByFtp(String remoteFileName) throws IOException {
        FtpUtils ftpUtils=new FtpUtils();
        String remotePath="/usr/app/ftp/root/org";
        String localPath="D://word//";
        ftpUtils.downloadFile(remotePath, remoteFileName, localPath);
        String path=localPath+remoteFileName;
        String code="utf8";
        List<OrgTree> list = changeToList(path, code);
        System.out.println("当前下载的数据条数："+list.size());

    }


    /**
     * 4aId查询组织树信息
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> selectByAreaId(Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        List<String> areaIds = (List<String>) params.get("areaIdList");
        List<Organization> organizations = new ArrayList<>();
        for (String areaId : areaIds){
            Organization organization = organizationMapper.selectBy4aId(Long.valueOf(areaId));
            if (organization!=null){
                organizations.add(organization);
            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg",organizations);
        return  maps;
    }

//    /**
//     * 通过父级菜单查询子菜单
//     * @param params
//     * @return
//     */
//    @Override
//    public Map<String,Object> selectBySumAreaId(Map<String, Object> params) {
//        Organization organization = null;
//        Long orgId = null;
//        Map<String, Object> maps = new HashMap<>();
//        List<Organization> list=new ArrayList<>();
//        List<String> areaList=(List<String>)params.get("areaId");
//        if (areaList!=null && areaList.size()>0){
//            list = organizationMapper.selectByParentId(Long.valueOf(areaList.get(0)));
//        }else {
////        SystemUserDto user = UserUtil.getUser();
//            SystemUserDto user = BssSessionHelp.getSystemUserDto();
//            orgId = user.getOrgId();
////        Long orgId = Long.valueOf(regionId1);
//            organization = organizationMapper.selectByPrimaryKey(orgId);
//        }
//        if (organization != null) {
//            Long regionId = organization.getRegionId();
//            String orgDivision = organization.getOrgDivision();
//            if (orgDivision.equals("10")){
//                orgId = ORG2RegionId.getOrgIdByRegionId(regionId);
//            }
//            List<Organization> organizations = organizationMapper.selectByParentId(orgId);
//            list.addAll(organizations);
//        }
//        Page pageInfo = new Page(new PageInfo(list));
//        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
//        maps.put("resultMsg",list);
//        return  maps;
//    }

    @Override
    public Map<String,Object> selectBySumAreaId(Map<String, Object> params) {
        Organization organization = null;
        Long orgId = null;
        Map<String, Object> maps = new HashMap<>();
        List<Organization> list=new ArrayList<>();
        List<String> areaList=(List<String>)params.get("areaId");
        if (areaList!=null && areaList.size()>0){
            list = organizationMapper.selectByParentId(Long.valueOf(areaList.get(0)));
        }else {
//        SystemUserDto user = UserUtil.getUser();
            SystemUserDto user = BssSessionHelp.getSystemUserDto();
            orgId = user.getOrgId();
            Long staffId = user.getStaffId();
            List<Map<String, Object>> staffOrgId = organizationMapper.getStaffOrgId(staffId);
            if (!staffOrgId.isEmpty() && staffOrgId.size() > 0){
                for (Map<String, Object> map : staffOrgId) {
                    Object orgDivision = map.get("orgDivision");
                    Object orgId1 = map.get("orgId");
                    if (orgDivision!=null){
                        if (orgDivision.toString().equals("30")) {
                            orgId = Long.valueOf(orgId1.toString());
                            break;
                        }else if (orgDivision.toString().equals("20")){
                            orgId = Long.valueOf(orgId1.toString());
                            break;
                        }else if (orgDivision.toString().equals("10")){
                            orgId = Long.valueOf(orgId1.toString());
                            break;
                        }
                    }
                }
            }
            if (orgId == null){
                list = organizationMapper.selectMenu();
            }else {
               list = organizationMapper.selectByParentId(orgId);
            }
        }
        Page pageInfo = new Page(new PageInfo(list));
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg",list);
        ////        Long orgId = Long.valueOf(regionId1);
//            organization = organizationMapper.selectByPrimaryKey(orgId);
//        }
//        if (organization != null) {
//            Long regionId = organization.getRegionId();
//            String orgDivision = organization.getOrgDivision();
//            if (orgDivision.equals("10")){
//                orgId = ORG2RegionId.getOrgIdByRegionId(regionId);
//            }
        return  maps;
    }

    /**
     * 分段插入
     * @param list
     */
    public void batchToInsert(List<OrgTree> list){
        if(list.size()<max){
            System.out.println("条数为："+list.size()+" 一次插入");
            addBatchData(list);
        }else{
            System.out.println("条数为："+list.size()+" 多次插入");
            //计算需要多少次操作  像上取整
            boolean tip=false;
            int remain=list.size()%max;
            if(remain!=0){
                tip=true;
            }
            int times=list.size()/max;
            if(tip){
                times+=1;
            }
            //分批次插入
            for (int i = 0; i <times ; i++) {
                //插入的集合
                List<OrgTree> insertList=new ArrayList<>();
                int maxBatch=(i+1)*max;
                int minBatch=i*max;
                if((i+1)==times){
                    //最后一批次有余数
                    for (int j = minBatch; j <list.size() ; j++) {
                        insertList.add(list.get(j));
                    }
                }else{
                    for (int j = minBatch; j <maxBatch ; j++) {
                        insertList.add(list.get(j));
                    }
                }
                //开始插入
                addBatchData(insertList);
            }

        }
    }


    /**
     * 将文件内容读取转换为对应bean的集合
     * @param path  文件路径
     * @param code  读取的文件的编码
     */
    public List<OrgTree> changeToList(String path,String code){
        List<OrgTree> list=new ArrayList<>();
        try {
            //获取转化bean的属性情况
            String className="com.zjtelcom.cpct.domain.org.OrgTree";
            Class<?> name =  Class.forName(className);
            Field[] declaredFields = name.getDeclaredFields();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path), code));

            String temp = null;
            while ((temp = bufferedReader.readLine()) != null) {
                OrgTree orgTree = changeToBean(temp,declaredFields.length);
                list.add(orgTree);
            }
            bufferedReader.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return list;
    }


    /**
     * 将字符串转换为bean
     * @param str
     * @return
     */
    public static OrgTree changeToBean(String str,int length) {
        SimpleDateFormat sf=new SimpleDateFormat();
        List<String> result = Arrays.asList(str.split(","));
        //先判断数据格式是否正确
        if(length!=result.size()){
            throw new SystemException("某行数据格式不正确");
        }
        OrgTree orgTree=new OrgTree();
        if(StringUtils.isBlank(result.get(0))){
            throw new SystemException("主键值不能为空");
        }
        orgTree.setAreaId(Integer.valueOf(result.get(0)));

        orgTree.setAreaName(result.get(1));
        if(StringUtils.isBlank(result.get(2))){
            orgTree.setSumAreaId(null);
        }else{
            orgTree.setSumAreaId(Integer.valueOf(result.get(2)));
        }

        if(StringUtils.isBlank(result.get(3))){
            orgTree.setAreaTypeId(null);
        }else{
            orgTree.setAreaTypeId(Integer.valueOf(result.get(3)));
        }

        orgTree.setState(result.get(4));

        if(StringUtils.isBlank(result.get(5))){
            orgTree.setStateDate(null);
        }else{
            orgTree.setStateDate(DateUtil.parseDate(result.get(5),"yyyy-MM-dd"));
        }

        if(StringUtils.isBlank(result.get(6))){
            orgTree.setLatnid(null);
        }else{
            orgTree.setLatnid(Integer.parseInt(result.get(6)));
        }

        if(StringUtils.isBlank(result.get(7))){
            orgTree.setServeTypeId(null);
        }else{
            orgTree.setServeTypeId(Integer.parseInt(result.get(7)));
        }


        orgTree.setCityType(result.get(8));
        orgTree.setViewTreeFlag(result.get(9));
        orgTree.setGridFlg(result.get(10));
        orgTree.setZjAreaFlg(result.get(11));
        orgTree.setBzjdFlg(result.get(12));
        orgTree.setStandardCode(result.get(13));
        orgTree.setXnFlag(result.get(14));
        orgTree.setBmFlag(result.get(15));
        orgTree.setAreaType(result.get(16));
        orgTree.setOrderId(result.get(17));
        orgTree.setSrzxFlg(result.get(18));
        orgTree.setCbjdFlg(result.get(19));
        orgTree.setTypeName(result.get(20));
        orgTree.setComments(result.get(21));
        orgTree.setGroupCode(result.get(22));
        orgTree.setCbjsFlg(result.get(23));
        return  orgTree;

    }


    public static  void test(){
        OrgTreeServiceImpl o=new OrgTreeServiceImpl();
        // D:\code\OFR_MKT_CHANNEL20180929.dat   org_tree.dat
        String path="D:\\code\\OFR_MKT_CHANNEL20180929.dat";
        long l = System.currentTimeMillis();
        System.out.println("读取开始");
        List<OrgTree> list=o.getDataByFtp(path);
        System.out.println("耗时："+(System.currentTimeMillis()-l));
        System.out.println("长度："+list.size());
        System.out.println(list.get(0).toString());
    }

    public static void main(String[] args) throws IOException {
        //test();
        OrgTreeServiceImpl org=new OrgTreeServiceImpl();
        long start=System.currentTimeMillis();
        String name="work.txt";
        org.getOrgTreeByFtp(name);
        System.out.println("耗时："+(System.currentTimeMillis()-start));
    }

    @Override
    public Map<String,Object> fuzzyQuery(Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        if(params == null || params.isEmpty()){
            map.put("resultCode", CommonConstant.CODE_SUCCESS);
            map.put("resultMsg","参数为空!");
            return map;
        }
        List<String> areaIds = new ArrayList<>();
        if(null != params.get("areaId") || ((List<String>)params.get("areaId")).size() > 0 ){
            areaIds = (List<String>)params.get("areaId");
        }
        Integer page = Integer.valueOf((String) params.get("page"));
        Integer pageSize = Integer.valueOf((String) params.get("pageSize"));
        PageHelper.startPage(page,pageSize);
        List<Map<String,String>> maps = organizationMapper.fuzzySelectByName(areaIds, params.get("fuzzyField") == null ? "":(String)params.get("fuzzyField"));
        Page pageInfo = new Page(new PageInfo(maps));
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg",maps);
        map.put("page",pageInfo);
        return map;
    }
}
