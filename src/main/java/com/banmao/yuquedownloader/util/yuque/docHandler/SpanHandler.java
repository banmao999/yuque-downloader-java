package com.banmao.yuquedownloader.util.yuque.docHandler;


import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SpanHandler extends DocHandler {

    {
        eleTagNames = new String[] {"span"};
    }

    @Override
    public void handleDocToMd(StringBuilder mdContent, Element element) {
        if (handlerParam.getStrong()) {
            log.debug("strong 已添加 {}", toc.getUrl());
            mdContent.append("**");
        }
        mdContent.append(element.html());
        if (handlerParam.getStrong()) {
            mdContent.append("**");
            handlerParam.setStrong(false);
        }
    }

}
