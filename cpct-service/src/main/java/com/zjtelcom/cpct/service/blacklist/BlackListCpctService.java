package com.zjtelcom.cpct.service.blacklist;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface BlackListCpctService {

    Map<String, Object> exportBlackListFile();

    Map<String, Object> importBlackListFile();

    Map<String,Object> getBlackListPageByKey( Map<String,Object> pageParams);

    /*cpc管理导出导入*/
    void exportBlackListFileManage(HttpServletResponse response) throws IOException;
    Map<String, Object> importBlackListFileManage(MultipartFile multipartFile) throws IOException;

}
