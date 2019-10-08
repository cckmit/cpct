package com.zjtelcom.cpct.dto.channel;

import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;

public class SystemUserVO extends SystemUserDto {
    private String c4CodeName;
    private String c5CodeName;


    public String getC4CodeName() {
        return c4CodeName;
    }

    public void setC4CodeName(String c4CodeName) {
        this.c4CodeName = c4CodeName;
    }

    public String getC5CodeName() {
        return c5CodeName;
    }

    public void setC5CodeName(String c5CodeName) {
        this.c5CodeName = c5CodeName;
    }
}
