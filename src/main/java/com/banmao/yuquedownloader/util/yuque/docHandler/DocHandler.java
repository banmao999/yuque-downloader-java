package com.banmao.yuquedownloader.util.yuque.docHandler;

import cn.hutool.extra.spring.SpringUtil;
import com.banmao.yuquedownloader.entity.AppData;
import com.banmao.yuquedownloader.entity.HandlerParam;
import com.banmao.yuquedownloader.util.YuQueUtil;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Element;

import java.util.Map;

@Getter
public abstract class DocHandler {

    @Setter
    protected HandlerParam handlerParam;

    @Setter
    protected AppData.Book.Toc toc;

    protected String[] eleTagNames = new String[] {};

    public abstract void handleDocToMd(StringBuilder mdContent, Element element);

    public void postHandleDocToMd(StringBuilder mdContent, Element element) {}

    public void wrapHandleDocToMd(StringBuilder mdContent, Element element) {

        handleDocToMd(mdContent, element);

        YuQueUtil.handleElements(element.children(), mdContent, toc, handlerParam);

        postHandleDocToMd(mdContent, element);
    }


    public static DocHandler getHandler(Element element) {

        Map<String, DocHandler> docHandlerMap = SpringUtil.getBeansOfType(DocHandler.class);
        for (DocHandler docHandler : docHandlerMap.values()) {
            for (String tagName : docHandler.getEleTagNames()) {
                if (tagName.equalsIgnoreCase(element.nodeName())) {
                    return docHandler;
                }
            }
        }

        return null;
    }

}
