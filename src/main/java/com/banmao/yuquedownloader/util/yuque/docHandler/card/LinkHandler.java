package com.banmao.yuquedownloader.util.yuque.docHandler.card;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.banmao.yuquedownloader.util.yuque.docHandler.CardHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LinkHandler extends CardHandler {

    {
        typeName = "bookmarklink";
    }

    @Override
    public void handleCardToMd(StringBuilder mdContent, String content) {
        log.debug("linkHandler: {}", content);
        JSONObject jsonObject = JSON.parseObject(content);
        Object src = jsonObject.get("src");
        Object text = jsonObject.get("text");
        if (mdContent.lastIndexOf("\n") == mdContent.length() - 1) {
            mdContent.replace(mdContent.length() - 1, mdContent.length(), "");
        }
        mdContent.append("[").append(text).append("](").append(src).append(")\n\n");

    }

}
