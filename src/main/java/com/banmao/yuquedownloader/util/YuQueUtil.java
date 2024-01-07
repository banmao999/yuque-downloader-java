package com.banmao.yuquedownloader.util;

import cn.hutool.core.io.FileUtil;
import com.banmao.yuquedownloader.entity.AppData;
import com.banmao.yuquedownloader.entity.HandlerParam;
import com.banmao.yuquedownloader.util.yuque.docHandler.DocHandler;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
@ConfigurationProperties(prefix = "yuque")
@PropertySource(value = "classpath:application.yaml")
public class YuQueUtil {

    @Getter
    private static String yuque_session;

    @Getter
    private static String acw_tc;

    @Getter
    private static String yuque_ctoken;

    @Getter
    private static String yuque_host;

    @Getter
    private static ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public void setYuque_session(String yuque_session) {
        YuQueUtil.yuque_session = yuque_session;
    }

    public void setAcw_tc(String acw_tc) {
        YuQueUtil.acw_tc = acw_tc;
    }

    public void setYuque_ctoken(String yuque_ctoken) {
        YuQueUtil.yuque_ctoken = yuque_ctoken;
    }

    public void setYuque_host(String yuque_host) {
        YuQueUtil.yuque_host = yuque_host;
    }

    @Resource
    @Qualifier("asyncImageExecutor")
    public void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        YuQueUtil.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    public static FakeBrowser fakeBrowser(String host) {
        HashMap<String, Object> cookies = new HashMap<>() {
            {
                put("_yuque_session", yuque_session);
//                put("acw_tc", acw_tc);
                put("yuque_ctoken", yuque_ctoken);
            }
        };
        FakeBrowser fakeBrowser = FakeBrowser.builder().host(host).cookies(cookies).build();
//        fakeBrowser.addHeader("Cookie", cookie);
        return fakeBrowser;
    }

    @Getter
    private static final ThreadLocal<CountDownLatch> COUNT_DOWN_LATCH_THREAD_LOCAL = new InheritableThreadLocal<>();

    public static CountDownLatch getCountDownLatch() {
        return COUNT_DOWN_LATCH_THREAD_LOCAL.get();
    }

    public static StringBuilder handleHtml(String html, AppData.Book.Toc toc) throws InterruptedException {
        Document document = Jsoup.parse(html);
        Element body = document.body();

        int imageCount = 0;
        // 计算总共有多少个 image 需要下载
        for (Element ele : body.getAllElements()) {
            if ("card".equalsIgnoreCase(ele.nodeName())) {
                String name = ele.attr("name");
                if ("image".equalsIgnoreCase(name)) {
                    imageCount++;
                }
            }
        }

        Elements children = body.children();
        StringBuilder mdContent = new StringBuilder();

        HandlerParam handlerParam = HandlerParam.builder().strong(false).headLevel(toc.getLevel() + 1).build();
        CountDownLatch countDownLatch = null;
        if (imageCount > 0) {
            countDownLatch = new CountDownLatch(imageCount);
            handlerParam.setImageCountDownLatch(countDownLatch);
        }

        preHandleTitle(mdContent, toc, handlerParam);
        handleElements(children, mdContent, toc, handlerParam);

        if (countDownLatch != null) {
            countDownLatch.await();
        }

        return mdContent;
    }

    private static void preHandleTitle(StringBuilder mdContent, AppData.Book.Toc toc, HandlerParam handlerParam) {
        String title = toc.getTitle();
        Integer headLevel = handlerParam.getHeadLevel();

        String wellSigns = YuQueUtil.genWellSigns(headLevel);
        mdContent.append(wellSigns).append(" ").append(title).append("\n\n");

    }

    public static void handleElements(Elements children, StringBuilder mdContent, AppData.Book.Toc toc, HandlerParam handlerParam) {
        for (Element child : children) {
            if (StringUtils.isBlank(child.nodeName())) {
                continue;
            }
            DocHandler handler = DocHandler.getHandler(child);
            if (handler != null) {
                handler.setToc(toc);
                handler.setHandlerParam(handlerParam);
                handler.wrapHandleDocToMd(mdContent, child);
            } else {
                log.warn("handler 获取失败,slug:{},nodeName:{}", toc.getUrl(), child.nodeName());
            }
        }
    }

    public static String genWellSigns(int count) {
        String sign = "";
        for (int i = 0; i < count; i++) {
            sign += "#";
        }
        return sign;
    }

    public static void handleImage(String src, String imageUrl, CountDownLatch imageCountDownLatch) {
        YuQueUtil.threadPoolTaskExecutor.execute(() -> {
            try {
                log.debug("开始处理图片: {}", src);
                FakeBrowser fakeBrowser = YuQueUtil.fakeBrowser(null);
                byte[] bytes = fakeBrowser.sendImageGet(src, null);
                if (bytes != null && bytes.length > 0) {
                    File file = FileUtil.writeBytes(bytes, imageUrl);
                    log.debug("图片处理完成: {}", file.getAbsolutePath());
                }
            } catch (Exception e) {
                log.error("图片下载失败,src:{},msg:{},error:", src, e.getMessage(), e);
            } finally {
                if (imageCountDownLatch != null) {
                    imageCountDownLatch.countDown();
//                    log.info("imageCountDownLatch: {} -- {}", imageUrl, imageCountDownLatch.getCount());
                }
            }
        });

    }

    public static void mergeMd2Book(String bookName) {
        String baseDir = bookName + File.separator + "md";
        boolean directory = FileUtil.isDirectory(baseDir);
        if (directory) {
            List<String> fileNames = FileUtil.listFileNames(baseDir);
            StringBuilder finalMdContent = new StringBuilder();
            fileNames.stream().sorted().forEach(fileName -> {
                String content = FileUtil.readString(baseDir + File.separator + fileName, StandardCharsets.UTF_8);
                finalMdContent.append(content);
            });
            FileUtil.writeString(finalMdContent.toString(), bookName + ".md", StandardCharsets.UTF_8);
        }
    }

}
