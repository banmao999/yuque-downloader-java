package com.banmao.yuquedownloader.entity;

import lombok.Data;

import java.util.List;

@Data
public class AppData {

    private Book book;

    @Data
    public static class Book {
        private Integer id;

        private String type;

        private String slug;

        private String name;

        private List<Toc> toc;

        @Data
        public static class Toc {

            private String type;

            private String title;

            private String uuid;

            private String url;

            private String bookName;

            private String pre_uuid;

            private String sibling_uuid;

            private String child_uuid;

            private String parent_uuid;

            private Integer doc_id;

            private Integer level;

            private Integer id;

            private Integer open_window;

            private Integer visible;

            private Integer idx;

        }
    }


}


