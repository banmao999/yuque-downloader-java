package com.banmao.yuquedownloader.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.CountDownLatch;

@Data
@Builder
@AllArgsConstructor
public class HandlerParam {

    private Boolean strong;

    private int headLevel;

    private ListType listType;

    private int olIdx = 0;

    private int imageIdx = 1;

    // 图片下载同步计算器
    private CountDownLatch imageCountDownLatch;

    public enum ListType {
        OL,
        UL
    }

}
