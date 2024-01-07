package com.banmao.yuquedownloader.util.yuque.docHandler;

import com.banmao.yuquedownloader.entity.HandlerParam;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OlUlHandler extends DocHandler {

    {
        eleTagNames = new String[] {"ol", "ul"};
    }

    @Override
    public void handleDocToMd(StringBuilder mdContent, Element element) {
//        mdContent.append(element.html());
        HandlerParam.ListType listType = handlerParam.getListType();
        if (listType != null && listType.equals(HandlerParam.ListType.OL)) {
            // <ol> 是数字列表
            handlerParam.setOlIdx(0);
        }
    }

    @Override
    public void postHandleDocToMd(StringBuilder mdContent, Element element) {
        handlerParam.setOlIdx(0);
        mdContent.append("\n\n");
    }

}
