package com.banmao.yuquedownloader.util.yuque.docHandler.card;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.banmao.yuquedownloader.util.yuque.docHandler.CardHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CodeblockHandler extends CardHandler {

    {
        typeName = "codeblock";
    }

    @Override
    public void handleCardToMd(StringBuilder mdContent, String content) {
        log.debug("CodeblockHandler: {}", content);
        JSONObject jsonObject = JSON.parseObject(content);
        Object code = jsonObject.get("code");
        Object mode = jsonObject.get("mode");
        Object name = jsonObject.get("name");
        if (!(code instanceof String)) {
            log.warn("codeHandler 未能解析:{}", content);
            return;
        }

        String codeStr = (String) code;
        mdContent.append("```").append((mode instanceof String m) ? m : "").append("\n");
        mdContent.append((name instanceof String m) && StringUtils.isNotBlank(m) ? "// " + m + "\n" : "");
        mdContent.append(codeStr).append("\n");
        mdContent.append("```").append("\n\n");

    }

}
