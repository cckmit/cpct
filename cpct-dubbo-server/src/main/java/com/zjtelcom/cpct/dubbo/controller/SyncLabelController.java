package com.zjtelcom.cpct.dubbo.controller;

import com.zjtelcom.cpct.dubbo.service.CatalogService;
import com.zjtelcom.cpct.dubbo.service.SyncLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/syncLabel")
public class SyncLabelController {
    @Autowired
    private SyncLabelService syncLabelService;







}
