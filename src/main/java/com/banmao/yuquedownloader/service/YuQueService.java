package com.banmao.yuquedownloader.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson2.JSON;
import com.banmao.yuquedownloader.entity.AppData;
import com.banmao.yuquedownloader.entity.Doc;
import com.banmao.yuquedownloader.util.FakeBrowser;
import com.banmao.yuquedownloader.util.YuQueUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Service
@Data
public class YuQueService {

//    private String cookie;

    @Resource
    @Qualifier("asyncDocExecutor")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;


    @PostConstruct
    public void construct() {
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setMaxPoolSize(20);
    }


    public Optional<AppData> getAppdata(String url) {
        FakeBrowser fakeBrowser = YuQueUtil.fakeBrowser(YuQueUtil.getYuque_host());
        String responseStr = fakeBrowser.sendGet(url, null);

        String pattern = "window.appData = JSON.parse\\(decodeURIComponent\\(\\\"(.*)\\\"\\)\\);";
        String resultStr = ReUtil.extractMulti(pattern, responseStr, "$1");
        if (StringUtils.isBlank(resultStr)) {
            return Optional.empty();
        }
        return Optional.ofNullable(JSON.parseObject(URLUtil.decode(resultStr), AppData.class));
    }

    @Async("asyncDocExecutor")
    public void handleDoc(AppData.Book book, AppData.Book.Toc toc) {
        try {
            String slug = toc.getUrl();
            FakeBrowser fakeBrowser = YuQueUtil.fakeBrowser(YuQueUtil.getYuque_host());

            HashMap<String, Object> params = new HashMap<>() {
                {
                    put("book_id", book.getId());
                }
            };
            String responseStr = fakeBrowser.sendGet("/api/docs/" + slug, params);

            Doc doc = JSON.parseObject(responseStr, Doc.class);

            if (doc.getData() == null) {
                log.warn("获取 docs 失败: {}", slug);
                String mdContent = YuQueUtil.genWellSigns(toc.getLevel() + 1) + " " + toc.getTitle();
                String format = String.format("%03d", toc.getIdx());
                File file = FileUtil.writeString(mdContent,
                        book.getName() + File.separator + "md" + File.separator + format + "-" + slug + ".md", StandardCharsets.UTF_8);
                log.debug("{} md 持久化: {}", toc.getTitle(), file.getAbsolutePath());
                return;
            }

            String format = String.format("%03d", toc.getIdx());

            String content = doc.getData().getContent();
            File file = FileUtil.writeString(content,
                    book.getName() + File.separator + format + "-" + slug + ".html", StandardCharsets.UTF_8);
            log.debug("{} doc 持久化: {}", doc.getData().getTitle(), file.getAbsolutePath());

            toc.setBookName(book.getName());
            StringBuilder mdContent = YuQueUtil.handleHtml(content, toc);

            file = FileUtil.writeString(mdContent.toString(),
                    book.getName() + File.separator + "md" + File.separator + format + "-" + slug + ".md", StandardCharsets.UTF_8);
            log.debug("{} md 持久化: {}", doc.getData().getTitle(), file.getAbsolutePath());

        } catch (Exception e) {
            log.error("处理 {} 文档报错,msg:{},error:", JSON.toJSONString(toc), e.getMessage(), e);
        } finally {
            YuQueUtil.getCountDownLatch().countDown();
            log.info("文档处理进度: {}/{}", YuQueUtil.getCountDownLatch().getCount(), YuQueUtil.getTOTAL_DOC_COUNT().get());
            log.debug("countDownLatch: {}", YuQueUtil.getCountDownLatch().getCount());
        }

    }

    @Async
    public void testAsync(int i) {
        System.out.println(i);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        CountDownLatch countDownLatch = YuQueUtil.getCountDownLatch();
        countDownLatch.countDown();

    }

    public void shutdownThreadPool() {
        threadPoolTaskExecutor.shutdown();
        if (YuQueUtil.getThreadPoolTaskExecutor() != null) {
            YuQueUtil.getThreadPoolTaskExecutor().shutdown();
        }
    }


}
