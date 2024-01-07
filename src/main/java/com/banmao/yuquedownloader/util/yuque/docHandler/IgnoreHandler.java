package com.banmao.yuquedownloader.util.yuque.docHandler;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

// 忽略 tag
@Slf4j
@Component
public class IgnoreHandler extends DocHandler {

    {
        eleTagNames = new String[] {"colgroup", "col", "tbody", "a"};
    }

    @Override
    public void handleDocToMd(StringBuilder mdContent, Element element) {

    }
}
