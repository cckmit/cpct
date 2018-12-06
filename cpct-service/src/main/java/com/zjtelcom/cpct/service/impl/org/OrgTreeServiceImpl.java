package com.zjtelcom.cpct.service.impl.org;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.org.OrgTreeMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.org.OrgTree;
import com.zjtelcom.cpct.domain.org.OrgTreeDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.org.OrgTreeService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.FtpUtils;
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
        List<SysParams> paramKeyIn = systemParamMapper.findParamKeyIn(path);
        if(!paramKeyIn.isEmpty()){
            code=paramKeyIn.get(0).getParamValue();
        }
        List<OrgTree> list = changeToList(path, code);
        long start=System.currentTimeMillis();
        //先删除该表所有数据
        orgTreeMapper.deleteAll();
        System.out.println("删除所有数据完毕");
        System.out.println("开始插入数据");
        //  sql上限  4194304   目前数据31008888(117530条数据,每条数据sql长度270)  计算一次插入1W条
        batchToInsert(list);
        System.out.println("数据插入完毕 耗时："+(System.currentTimeMillis()-start));

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
     * 通过父级菜单查询子菜单
     * @param params
     * @return
     */
    @Override
    public Map<String,Object> selectBySumAreaId(Map<String, Object> params) {
        String areaId= (String) params.get("areaId");
        String page= (String) params.get("page");
        String pageSize= (String) params.get("pageSize");
        Integer id=null;
        Integer pageId=0;
        Integer pageSizeId=0;
        //如果page 和pageSize为空时 传回所有数据
        if(StringUtils.isNotBlank(page)&&StringUtils.isNotBlank(pageSize)){
            pageId=Integer.parseInt(page);
            pageSizeId=Integer.parseInt(pageSize);
        }
        if(StringUtils.isNotBlank(areaId)){
            id=Integer.parseInt(areaId);
        }


        Map<String, Object> maps = new HashMap<>();
        boolean tip=false;
        if(pageId!=0) {
            PageHelper.startPage(pageId, pageSizeId);
            tip=true;
        }
        List<OrgTreeDO> list=new ArrayList<>();
        if(id==null){
            list=orgTreeMapper.selectMenu();
        }else{
            list=orgTreeMapper.selectBySumAreaId(id);
        }
        Page pageInfo = new Page(new PageInfo(list));
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg",list);
        if(tip){
            maps.put("page",pageInfo);
        }
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
}
