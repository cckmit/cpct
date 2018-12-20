package com.zjtelcom.cpct.open.base.generator;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 自定义模板生产工具类
 */
public class MyGenerator {

    //公用包名
    private static String packName="com.zjtelcom.cpct.open";


    /**
     * 按规范模块生成 实体类  接口  接口实现类  控制层
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Map<String,String> map=new HashMap<>();
        //---------------------需要修改的地方如下------------------------------
        //贴近实际类的上一层目录名 自定义的
        map.put("catalogName","mktQuestionnaire");
        //openapi模块对应实体类名 自定义的  Event对应接口名字为OpenEventService
        map.put("entityName","MktQuestionnaire");
        //注释  自定义的
        map.put("annotation","调研问卷");

        //---------------------需要去其他模块找对应名字的------------------------
        //对应引用cpct-common的javabean  因为cpct-common里面命名不是特别有规律  需要多写两层目录!!!一定要多写两层目录
        map.put("beanCatalogName","domain.question.Questionnaire");
        //对应引用cpct-common的mapper名  命名不规范啊  一定要写满上两层
        map.put("mapperCatalogName","dao.question.MktQuestionnaireMapper");


        //---------------------需要去openapi文档中找名字的----------------------
        //openapi路径请求名    openapi文档中找的
        map.put("path","mktQuestionnaire");
        //openapi路径附带id名  openapi文档中找的
        map.put("id","id");



        //生成接口
        generatorService(map,"service.vm");
        //生成实现类
        generatorService(map,"serviceImpl.vm");
        //生成控制层
        generatorService(map,"controller.vm");
        //生产集团规范返回的实体类
        generatorService(map,"entity.vm");
    }




    /**
     * 生成模板
     * @param map           vm参数
     * @param vmName        要解析的模板的名称
     */
    public static void generatorService(Map<String,String> map,String vmName) throws Exception {
        String entityName=map.get("entityName");
        String catalogName=map.get("catalogName");
        String annotation=map.get("annotation");
        String path=map.get("path");
        String id=map.get("id");
        String beanCatalogName=map.get("beanCatalogName");
        String mapperCatalogName=map.get("mapperCatalogName");

        //通过javabean得到对应的bean名字和其目录
        String[] split = beanCatalogName.split("\\.");
        String javabean="";              //javabean名字
        String catalogMapper="";         //mapper的上一层目录名
        for (int i = 0; i <split.length ; i++) {
            if(i==1){
                catalogMapper=split[i];
            }else if(i==2){
                javabean=split[i];
            }
        }

        //找到对应的mapper名字
        String mapperName="";
        String[] s = mapperCatalogName.split("\\.");
        for (int i = 0; i <s.length ; i++) {
            if(i==2){
                mapperName=s[i];
            }
        }


        String sourcePath = System.getProperty("user.dir")+"\\cpct-open-api\\src\\main\\resources\\vm\\";            //模板绝对路径文件夹
        String targetPath = System.getProperty("user.dir")
                + "\\cpct-open-api\\src\\main\\java\\" + "\\"
                + packName.replace(".", "\\");                           //生成文件位置  绝对路径

        //生成名
        String targetFile = "";
        if(vmName.equals("service.vm")){
            targetFile = "Open"+entityName + "Service.java";
            targetPath=targetPath+"\\service\\"+catalogName;
        }else if(vmName.equals("serviceImpl.vm")){
            targetFile = "Open"+entityName + "ServiceImpl.java";
            targetPath=targetPath+"\\serviceImpl\\"+catalogName;
        }else if(vmName.equals("controller.vm")){
            targetFile = "Open"+entityName + "Controller.java";
            targetPath=targetPath+"\\controller\\"+catalogName;
        }else{
            targetFile = "Open"+entityName+".java";
            targetPath=targetPath+"\\entity\\"+catalogName;
        }
        System.out.println(sourcePath);
        System.out.println(targetPath);
        Properties pro = new Properties();
        pro.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        pro.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, sourcePath);
        VelocityEngine ve = new VelocityEngine(pro);

        //设置变量
        VelocityContext context = new VelocityContext();
        context.put("packageName",packName);
        context.put("entityName",entityName);            //Channel
        context.put("catalogName",catalogName);          //channel
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        context.put("date",sdf.format(new Date()));
        context.put("annotation",annotation);            //渠道
        context.put("path",path);
        context.put("id",id);

        context.put("beanCatalogName",beanCatalogName);  //domain.channel.Channel
        context.put("javabean",javabean);                //Channel
        context.put("catalogMapper",catalogMapper);      //channel

        context.put("mapperCatalogName",mapperCatalogName);      //dao.event.ContactEvtMapper
        context.put("mapperName",mapperName);                    //ContactEvtMapper
        //模板名
        Template t = ve.getTemplate(vmName, "UTF-8");

        //生成位置
        File file = new File(targetPath, targetFile);
        createVm(t,file,context);
        System.out.println("成功生成文件:"
                + (targetPath + targetFile).replaceAll("/", "\\\\"));

    }





    /**
     * 解析vm模板
     * @param t        模板名  如mbg.vm
     * @param file     生成的文件  位置和文件名
     * @param context  VelocityContext 模板变量
     * @throws Exception
     */
    public static void createVm(Template t,File file,VelocityContext context ) throws Exception {
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (!file.exists())
            file.createNewFile();

        FileOutputStream outStream = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(outStream,
                "UTF-8");
        BufferedWriter sw = new BufferedWriter(writer);
        t.merge(context, sw);
        sw.flush();
        sw.close();
        outStream.close();
    }


    //demo
    public static void demo() throws Exception {
        Map<String,String> map=new HashMap<>();
        //---------------------需要修改的地方如下------------------------------
        //贴近实际类的上一层目录名 自定义的
        map.put("catalogName","event");
        //openapi模块对应实体类名 自定义的  Event对应接口名字为OpenEventService
        map.put("entityName","Event");
        //注释  自定义的
        map.put("annotation","事件");

        //---------------------需要去其他模块找对应名字的------------------------
        //对应引用cpct-common的javabean  因为cpct-common里面命名不是特别有规律  需要多写两层目录!!!一定要多写两层目录
        map.put("beanCatalogName","dto.event.ContactEvt");
        //对应引用cpct-common的mapper名  命名不规范啊  一定要写满上两层
        map.put("mapperCatalogName","dao.event.ContactEvtMapper");


        //---------------------需要去openapi文档中找名字的----------------------
        //openapi路径请求名    openapi文档中找的
        map.put("path","event");
        //openapi路径附带id名  openapi文档中找的
        map.put("id","id");



        //生成接口
        generatorService(map,"service.vm");
        //生成实现类
        generatorService(map,"serviceImpl.vm");
        //生成控制层
        generatorService(map,"controller.vm");
    }

}
