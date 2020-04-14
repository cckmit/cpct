package com.zjtelcom.cpct.service.impl.grouping;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.grouping.ServicePackageMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.grouping.ServicePackage;
import com.zjtelcom.cpct.dto.channel.TransDetailDataVO;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.MqService;
import com.zjtelcom.cpct.service.grouping.ServicePackageService;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UserUtil;
import com.zjtelcom.es.es.service.EsServicePackageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/08/08 17:17
 * @version: V1.0
 */
@Service
public class ServicePackageServiceImpl implements ServicePackageService {

    public static final Logger logger = LoggerFactory.getLogger(ServicePackageServiceImpl.class);


    @Autowired
    private ServicePackageMapper servicePackageMapper;

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;

    @Autowired(required = false)
    private EsServicePackageService esServicePackageService;

    @Autowired
    private MqService mqService;

    @Autowired
    private RedisUtils redisUtils;

    @Value("${ctg.cpctSerpackageTopic}")
    private String cpctSerpackageTopic;

    private final static int NUM = 2000;

    @Override
    public Map<String, Object> saveServicePackage(String name, MultipartFile multipartFile) {
        Map<String, Object> maps = new HashMap<>();
        try {

            // 解析服务包 xlsx文件
            XlsxProcessAbstract xlsxProcess = new XlsxProcessAbstract();
            TransDetailDataVO dataVO = xlsxProcess.processAllSheet(multipartFile);
            List<String> contentList = dataVO.getContentList();
            // 获取标签code
            String labelCode = contentList.get(2).split("\\|@\\|")[0];


            //查询是否为我们库中的标签
            List<Label> labelList = injectionLabelMapper.selectAll();
            boolean isExit = false;
            for (Label label : labelList) {
                if (labelCode.equals(label.getInjectionLabelCode())) {
                    isExit = true;
                    break;
                }
            }
            if (!isExit) {
                maps.put("resultCode", CommonConstant.CODE_FAIL);
                maps.put("resultMsg", "不存在该标签！");
                return maps;
            }


            ServicePackage servicePackage = new ServicePackage();
            servicePackage.setServicePackageName(name);
            servicePackage.setLabel(labelCode);
            servicePackage.setCreateDate(new Date());
            servicePackage.setCreateStaff(UserUtil.loginId());
            servicePackage.setUpdateDate(new Date());
            servicePackage.setUpdateStaff(UserUtil.loginId());
            servicePackage.setStatusCd(StatusCode.STATUS_CODE_NOTACTIVE.getStatusCode());
            servicePackage.setStatusDate(new Date());
            servicePackageMapper.insert(servicePackage);

            // 获取服务包Id
            Long servicePackageId = servicePackage.getServicePackageId();

            // 创建索引
            esServicePackageService.servicePackageInport(servicePackageId, new ArrayList<>());

            logger.info("服务包清单contentList数量：" + contentList.size());
            int mqSum = 0;
            int total = contentList.size() / NUM;
            if (contentList.size() % NUM > 0) {
                total++;
            }
            logger.info("有 " + total +" 批的服务包数据");
            for (int i = 0; i < total; i++) {
                List<String> newContentList = new ArrayList();
                if (i == 0) {
                    if (i == total - 1) {
                        newContentList = contentList.subList(2, contentList.size());
                        System.out.println(newContentList.get(0));
                    } else {
                        newContentList = contentList.subList(2, NUM);
                    }
                } else{
                    if (i == total - 1) {
                        newContentList = contentList.subList(i * NUM, contentList.size());
                    } else {
                        newContentList = contentList.subList(i * NUM, (i + 1) * NUM);
                    }
                }
                System.out.println(" i = " + i);
                logger.info("newContentList.size() = " + newContentList.size());
                // 向MQ中扔入contentList
                HashMap msgBody = new HashMap();
                msgBody.put("servicePackageId", servicePackageId);
                msgBody.put("labelCode", labelCode);
                msgBody.put("contentList", newContentList);
                try {
                    // 判断是否发送成功
                    if (!mqService.msg2Producer(msgBody, cpctSerpackageTopic, servicePackageId.toString(), labelCode).equals("SEND_OK")) {
                        // 发送失败自动重发2次，如果还是失败，记录
                        logger.error("CTGMQ消息生产失败,servicePackageId:" + servicePackageId, JSON.toJSONString(msgBody));
                    }
                    mqSum++;
                    msgBody = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            redisUtils.set("MQ_SERPACK_SUM_" + servicePackageId, mqSum);

            // 异步调用es的dubbo服务，入参 servicePackageId, contentList
/*
            new Thread() {
                @Override
                public void run() {
                    esServicePackageService.servicePackageInport(servicePackageId, contentList);
                }
            }.start();
*/



            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", "添加服务包成功！");
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "添加服务包列表失败！");
        } finally {
            return maps;
        }
    }


    @Override
    public Map<String, Object> deleteServicePackage(Long servicePackageId) {
        Map<String, Object> maps = new HashMap<>();
        try {
            servicePackageMapper.deleteByPrimaryKey(servicePackageId);
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", "删除服务包成功！");
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "删除服务包列表失败！");
        } finally {
            return maps;
        }
    }


    @Override
    public Map<String, Object> getServicePackageList(Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        List<ServicePackage> servicePackageList = new ArrayList<>();
        try {
            PageHelper.startPage(Integer.parseInt(params.get("page").toString()), Integer.parseInt(params.get("pageSize").toString()));
            servicePackageList = servicePackageMapper.selectByName((String) params.get("servicePackageName"));
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", "查询服务包列表成功！");
            maps.put("servicePackageList", servicePackageList);
            maps.put("pageInfo", new Page(new PageInfo(servicePackageList)));
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "查询服务包列表失败！");
        } finally {
            return maps;
        }
    }

    @Override
    public Map<String, Object> selectByName(String servicePackageName) {
        Map<String, Object> maps = new HashMap<>();
        List<ServicePackage> servicePackageList = new ArrayList<>();
        try {
            servicePackageList = servicePackageMapper.selectByName(servicePackageName);
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", "查询服务包列表成功！");
            maps.put("servicePackageList", servicePackageList);
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "查询服务包列表失败！");
        } finally {
            return maps;
        }
    }


}