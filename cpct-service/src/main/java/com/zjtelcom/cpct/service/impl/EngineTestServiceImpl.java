package com.zjtelcom.cpct.service.impl;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.zjtelcom.cpct.service.EngineTestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EngineTestServiceImpl implements EngineTestService {

    @Override
    public void test() {

        try {
            ExpressRunner runner = new ExpressRunner();
            DefaultContext<String, Object> context = new DefaultContext<String, Object>();
            context.put("a",1);
            context.put("b",2);
            context.put("c",3);
            String express = "a+b*c";
            Object r = runner.execute(express, context, null, true, false);
            System.out.println(r);



        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
