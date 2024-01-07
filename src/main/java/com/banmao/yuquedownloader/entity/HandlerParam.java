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

    @Builder.Default
    private int olIdx = 0;

    @Builder.Default
    private int imageIdx = 1;

    @Builder.Default
    private Boolean initTable = false;
    @Builder.Default
    private Boolean pInTd = false;

    // 图片下载同步计算器
    private CountDownLatch imageCountDownLatch;

    public enum ListType {
        OL,
        UL
    }

}
