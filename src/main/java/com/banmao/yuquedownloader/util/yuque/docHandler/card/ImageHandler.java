package com.banmao.yuquedownloader.util.yuque.docHandler.card;

import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.banmao.yuquedownloader.entity.AppData;
import com.banmao.yuquedownloader.entity.HandlerParam;
import com.banmao.yuquedownloader.util.YuQueUtil;
import com.banmao.yuquedownloader.util.yuque.docHandler.CardHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ImageHandler extends CardHandler {

    {
        typeName = "image";
    }

    @Override
    public void handleCardToMd(StringBuilder mdContent, String content) {
        log.debug("ImageHandler: {}", content);
        JSONObject jsonObject = JSON.parseObject(content);
        Object srcObj = jsonObject.get("src");
        Object name = jsonObject.get("name");
        if (srcObj instanceof String src) {

            int imageIdx = handlerParam.getImageIdx();
            String format = String.format("%03d", toc.getIdx());
            String imageName = format + "-" + toc.getTitle() + "-" + imageIdx +
                    (StringUtils.isNotBlank(String.valueOf(name).replace("null", "")) ? name : "pic.png");
            String imageUrl = genImageUrl(toc, imageName, handlerParam);

            YuQueUtil.handleImage(src, imageUrl, handlerParam.getImageCountDownLatch());

            mdContent.append("![").append(imageName).append("](").append(URLUtil.encode(imageUrl)).append(")\n\n");
            handlerParam.setImageIdx(imageIdx + 1);
        }

    }

    private static String genImageUrl(AppData.Book.Toc toc, String imageName, HandlerParam handlerParam) {
        String url = "." + File.separator + toc.getBookName() + ".assets" +
                File.separator + imageName;

        return url;
    }

}
