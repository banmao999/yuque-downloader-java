package com.banmao.yuquedownloader.util.yuque.docHandler;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CodeHandler extends DocHandler {

    {
        eleTagNames = new String[] {"code"};
    }

    @Override
    public void handleDocToMd(StringBuilder mdContent, Element element) {

        mdContent.append("`");

    }

    @Override
    public void postHandleDocToMd(StringBuilder mdContent, Element element) {
        mdContent.append("`").append("\n\n");
    }
}
