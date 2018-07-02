package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dto.channel.VerbalAddVO;
import com.zjtelcom.cpct.dto.channel.VerbalEditVO;
import com.zjtelcom.cpct.dto.channel.VerbalVO;

import java.util.List;
import java.util.Map;

public interface VerbalService {

    Map<String,Object> addVerbal(Long userId, VerbalAddVO addVO);

    Map<String,Object> editVerbal(Long userId, VerbalEditVO editVO);

    Map<String,Object> getVerbalDetail(Long userId,Long verbalId);

    Map<String,Object> getVerbalListByConfId(Long userId,Long confId);



}
