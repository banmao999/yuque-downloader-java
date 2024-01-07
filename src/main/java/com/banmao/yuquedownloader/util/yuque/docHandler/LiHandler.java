package com.banmao.yuquedownloader.util.yuque.docHandler;

import com.banmao.yuquedownloader.entity.HandlerParam;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LiHandler extends DocHandler {

    {
        eleTagNames = new String[] {"li"};
    }

    @Override
    public void handleDocToMd(StringBuilder mdContent, Element element) {
        HandlerParam.ListType listType = handlerParam.getListType();
        if (listType != null && listType.equals(HandlerParam.ListType.OL)) {
            // <ol> 是数字列表
            int olIdx = handlerParam.getOlIdx();
            mdContent.append(olIdx).append(" ");
            handlerParam.setOlIdx(olIdx + 1);
        } else {
            mdContent.append("- ");
        }
    }

    @Override
    public void postHandleDocToMd(StringBuilder mdContent, Element element) {
        mdContent.append("\n");
    }
}
