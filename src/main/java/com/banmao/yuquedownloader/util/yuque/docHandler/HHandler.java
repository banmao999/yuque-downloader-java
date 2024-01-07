package com.banmao.yuquedownloader.util.yuque.docHandler;

import com.banmao.yuquedownloader.util.YuQueUtil;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HHandler extends DocHandler {

    {
        eleTagNames = new String[] {"h", "h1", "h2", "h3", "h4",
                "h5", "h6", "h7", "h8", "h9", "h10"};
    }

    @Override
    public void handleDocToMd(StringBuilder mdContent, Element element) {
        Integer headLevel = handlerParam.getHeadLevel();
        String hLevel = element.nodeName().replace("h", "");
        headLevel = Integer.parseInt(hLevel) - 1 + headLevel;

        String wellSigns = YuQueUtil.genWellSigns(headLevel);

        mdContent.append(wellSigns).append(" ");
    }

    @Override
    public void postHandleDocToMd(StringBuilder mdContent, Element element) {
        mdContent.append("\n\n");
    }
}
