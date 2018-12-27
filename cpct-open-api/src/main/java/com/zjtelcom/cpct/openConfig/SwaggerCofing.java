package com.zjtelcom.cpct.openConfig;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @Auther: anson
 * @Date: 2018/12/17
 * @Description:   访问地址http://localhost:8090/swagger-ui.html#
 */
public class SwaggerCofing {

//    @Bean
//    public Docket apiConfig(){
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select()//创建ApiSelectorBuilder对象
//                .paths(Predicates.or(PathSelectors.regex("/api2/.*"))).build()//过滤的接口
//                .groupName("myapi") //定义分组
//                .apiInfo(apiInfo())// 调用apiInfo方法,创建一个ApiInfo实例,里面是展示在文档页面信息内容
//                .useDefaultResponseMessages(false)//关闭默认返回值
//                ;
//    }
//
//    @Bean
//    public Docket wap_api() {
//        return new Docket(DocumentationType.SWAGGER_2)
//                .select().apis(RequestHandlerSelectors.any())
//                .paths(PathSelectors.ant("/api/**")).build()//选定api的路径
//                .groupName("WEB接口文档V4.4").pathMapping("/")//创建第二个分组
//                .apiInfo(apiInfo());
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("BookStore Platform API")//大标题
//                .description("BookStore Platform's REST API, all the applications could access the Object model data via JSON.")//详细描述
//                .version("2.0")//版本
//                .contact(new Contact("Helen", "http://qfedu.com", "123456@qq.com"))//作者
//                .license("The Apache License, Version 2.0")//许可证信息
//                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")//许可证地址
//                .build();
//    }


}
