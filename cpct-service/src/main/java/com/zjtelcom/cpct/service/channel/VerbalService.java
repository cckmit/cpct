package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dto.channel.VerbalAddVO;
import com.zjtelcom.cpct.dto.channel.VerbalEditVO;
import com.zjtelcom.cpct.dto.channel.VerbalVO;

import java.util.List;

public interface VerbalService {

    RespInfo addVerbal(Long userId, VerbalAddVO addVO);

    RespInfo editVerbal(Long userId, VerbalEditVO editVO);

    RespInfo getVerbalDetail(Long userId,Long verbalId);

    RespInfo getVerbalListByConfId(Long userId,Long confId);



}
