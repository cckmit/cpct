package com.zjtelcom.cpct.service.dubbo;

import java.util.Map;

public interface UCCPService {

    void sendShortMessage(String targPhone, String sendContent, String lanId) throws Exception;

}
