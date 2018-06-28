package com.zjtelcom.cpct.service.impl;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.EngineTestService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EngineTestServiceImpl extends BaseService implements EngineTestService {

    @Override
    public void test() {

        try {
            ExpressRunner runner = new ExpressRunner();
            DefaultContext<String, Object> context = new DefaultContext<String, Object>();

//            context.put("a",1);
//            context.put("b",2);
//            context.put("c",3);
//            String express = "a+b*c";
//            Object r = runner.execute(express, context, null, true, false);
//            System.out.println(r);

            //年溢出
            String label1 = "label1";
            //连续两个月
            String label2 = "label2";
            //订购
            String label3 = "label3";

            context.put(label1,6);
            context.put(label2,1);
            context.put(label3,1);

            String express = "label1>=6&&label2==1&&label3==1";

            Object r = runner.execute(express, context, null, true, false);
            System.out.println(r);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
