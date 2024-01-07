package com.banmao.yuquedownloader.util.yuque.docHandler;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class CardHandler extends DocHandler {

    {
        eleTagNames = new String[] {"card"};
    }

    @Override
    public void handleDocToMd(StringBuilder mdContent, Element element) {
        CardHandler cardHandler = getType(element);
        if (cardHandler == null) {
            log.warn("cardHandler 获取失败:{}", element.attr("name"));
            return;
        }
        cardHandler.setToc(toc);
        cardHandler.setHandlerParam(handlerParam);

        String value = element.attr("value");
        String uriStr = ReUtil.extractMulti("data:(.*)", value, "$1");
        String content = URLUtil.decode(uriStr);
        cardHandler.handleCardToMd(mdContent, content);

    }

    @Getter
    protected String typeName = "";

    public abstract void handleCardToMd(StringBuilder mdContent, String content);

    private CardHandler getType(Element element) {
        String type = element.attr("name");
        Assert.isTrue(StringUtils.isNoneBlank(type), "未解析到正确的 Card 格式");

        Map<String, CardHandler> cardHandlerMap = SpringUtil.getBeansOfType(CardHandler.class);
        for (CardHandler cardHandler : cardHandlerMap.values()) {
            if (cardHandler.getTypeName().equalsIgnoreCase(type)) {
                return cardHandler;
            }
        }

        return null;
    }

}
