package com.zjtelcom.cpct.service.blacklist;

import java.util.Map;

public interface BlackListCpctService {

    Map<String, Object> exportBlackListFile();

    Map<String, Object> importBlackListFile();

}
