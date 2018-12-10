package com.gzs.learn.serial;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.gzs.learn.serial.domain.DataStatus;
import com.gzs.learn.serial.domain.SerialGroup;
import com.gzs.learn.serial.dubbo.DubboSerialManagerService;

public class SerialDistributeClient {
    private static ClassPathXmlApplicationContext context;

    public static void main(String[] args) throws Exception {
        context = new ClassPathXmlApplicationContext(
                new String[] {"/META-INF/SerialDistributeClient.xml"});
        context.start();
        DubboSerialManagerService service = context.getBean(DubboSerialManagerService.class);
        SerialGroup group = new SerialGroup();
        group.setName("order");
        group.setStat(DataStatus.ENABLE);
        group.setVersion(0);
        group.setStep(100000);
        group.setMin(674807165557424129L);
        group.setMax(674807199917162495L);
        group.setPartition(32);
        group.setTsup(System.currentTimeMillis());
        group.setUpid(1);
        service.createSerialGroup(group);
    }
}
