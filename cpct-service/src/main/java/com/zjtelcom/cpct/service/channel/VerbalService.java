package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.dto.channel.VerbalAddVO;
import com.zjtelcom.cpct.dto.channel.VerbalEditVO;

import java.util.Map;

public interface VerbalService {

    Map<String,Object> addVerbal(Long userId, VerbalAddVO addVO);

    Map<String,Object> editVerbal(Long userId, VerbalEditVO editVO);

    Map<String,Object> getVerbalDetail(Long userId,Long verbalId);

    Map<String,Object> getVerbalListByConfId(Long userId,Long confId);

    Map<String,Object> delVerbal(Long userId,Long verbalId);

    Map<String,Object> copyVerbal(Long contactConfId,Long newConfId);

    Map<String,Object> copyVerbalFromRedis(Long contactConfId,Long newConfId);



}
