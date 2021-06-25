package com.tanhua.manage.test;

import com.tanhua.commons.templates.HuaWeiUGCTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HuaWeiTest {

    @Autowired
    private HuaWeiUGCTemplate template;

    @Test
    public void testToken() {
        System.out.println(template.getToken());
    }

    @Test
    public void testText() {
        boolean check = template.textContentCheck("好好先生");
        System.out.println(check);
    }

    @Test
    public void testImages() {
        String[] urls = new String[]{
                "https://tanhua111danshui.oss-cn-shenzhen.aliyuncs.com/images/2021/06/18/6c31358e-8dc0-49ed-8192-4054a06708a1.jpg"

        };
        boolean check = template.imageContentCheck(urls);
        System.out.println(check);
    }
}