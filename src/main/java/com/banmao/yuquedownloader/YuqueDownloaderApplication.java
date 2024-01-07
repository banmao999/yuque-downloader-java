package com.banmao.yuquedownloader;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson2.JSON;
import com.banmao.yuquedownloader.entity.AppData;
import com.banmao.yuquedownloader.service.YuQueService;
import com.banmao.yuquedownloader.util.YuQueUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

@Slf4j
@EnableAsync
@SpringBootApplication
public class YuqueDownloaderApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(YuqueDownloaderApplication.class, args);
    }

    @Resource
    private YuQueService yuQueService;

    @Override
    public void run(String... args) throws Exception {
//        YuQueUtil.mergeMd2Book("Spring源码解析");
//
//
//        if (true) {
//            return;
//        }

        Optional<AppData> appdata = yuQueService.getAppdata("/jiangnanyidianyujavaboyorg/kzmgpd/oq4ggkz891zkiocb");

        appdata.ifPresent(data -> {
            String bookName = data.getBook().getName();

            FileUtil.clean(bookName);

            String filePath = bookName + File.separator + data.getBook().getId() + "-appdata.json";
            File file = FileUtil.writeString(JSON.toJSONString(data), filePath, StandardCharsets.UTF_8);
            log.debug("appdata持久化: {}", file.getAbsolutePath());

            List<AppData.Book.Toc> tocList = data.getBook().getToc();
            YuQueUtil.getCOUNT_DOWN_LATCH_THREAD_LOCAL().set(new CountDownLatch(tocList.size()));

            int index = 0;
            for (AppData.Book.Toc toc : tocList) {
                toc.setIdx(++index);
                yuQueService.handleDoc(data.getBook(), toc);
            }

            try {
                YuQueUtil.getCountDownLatch().await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // 整合全部 md 文件
            YuQueUtil.mergeMd2Book(bookName);

            log.info("========> 文档处理已完成");
            yuQueService.shutdownThreadPool();

        });
    }
}
