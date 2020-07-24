package com.zjtelcom.cpct.service.blacklist;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface BlackListCpctService {

    Map<String, Object> exportBlackListFile();

    Map<String, Object> importBlackListFile();
    /*cpc管理导出导入*/
    Map<String,Object> deleteBlackList(List<String> phoneNumsDeleted);
    Map<String,Object> getBlackListPageByKey( Map<String,Object> pageParams);
    void exportBlackListFileManage(HttpServletResponse response) throws IOException;
    Map<String, Object> importBlackListFileManage(MultipartFile multipartFile) throws IOException;
    Map<String,Object> addBlackList(List<Map<String, Object>> blackListContent);


}
