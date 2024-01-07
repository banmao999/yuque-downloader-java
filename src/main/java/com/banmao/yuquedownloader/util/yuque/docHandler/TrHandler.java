package com.banmao.yuquedownloader.util.yuque.docHandler;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TrHandler extends DocHandler {

    {
        eleTagNames = new String[]{"tr"};
    }

    @Override
    public void handleDocToMd(StringBuilder mdContent, Element element) {
//        log.info("trHandler...");
    }

    @Override
    public void postHandleDocToMd(StringBuilder mdContent, Element element) {
        mdContent.append("|\n");
        if (handlerParam.getInitTable()) {
            int tdCount = 0;
            Elements allElements = element.getAllElements();
            for (Element ele : allElements) {
                if ("td".equalsIgnoreCase(ele.nodeName())) {
                    tdCount++;
                }
            }
            mdContent.append("|---".repeat(Math.max(0, tdCount)));
            mdContent.append("|\n");
            handlerParam.setInitTable(false);
        }
    }
}
