package com.banmao.yuquedownloader.util.yuque.docHandler;

import org.jsoup.nodes.Element;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PBrHandler extends DocHandler{

    {
        eleTagNames = new String[] {"p", "br"};
    }

    @Override
    public void handleDocToMd(StringBuilder mdContent, Element element) {
//        mdContent.append(element.html());
    }

    @Override
    public void postHandleDocToMd(StringBuilder mdContent, Element element) {
        mdContent.append("\n");
        if ("p".equalsIgnoreCase(element.nodeName())) {
            mdContent.append("\n");
        }
    }

}
