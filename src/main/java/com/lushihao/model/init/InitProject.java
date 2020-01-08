package com.lushihao.model.init;

import com.lushihao.model.quartz.config.JobClassesConfig;
import com.lushihao.model.quartz.config.QuartzConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class InitProject implements ApplicationRunner {
    @Autowired
    private QuartzConfig quartzConfig;
    @Autowired
    private JobClassesConfig jobClassesConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Map<String, String>> list = jobClassesConfig.getJobList();
        quartzConfig.startJob(list);
    }
}