package com.banmao.yuquedownloader.util.yuque.docHandler;

import org.jsoup.nodes.Element;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TableHandler extends DocHandler{

    {
        eleTagNames = new String[] {"table"};
    }

    @Override
    public void handleDocToMd(StringBuilder mdContent, Element element) {
//        mdContent.append(element.html());
        handlerParam.setInitTable(true);
    }

    @Override
    public void postHandleDocToMd(StringBuilder mdContent, Element element) {
        mdContent.append("\n");
    }
}
