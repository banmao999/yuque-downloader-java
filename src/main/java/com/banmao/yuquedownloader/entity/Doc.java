package com.banmao.yuquedownloader.entity;

import lombok.Data;

@Data
public class Doc {

    private DocData data;

    @Data
    public static class DocData {
        private String type;

        private String title;

        private String slug;

        private String content;
    }

}
