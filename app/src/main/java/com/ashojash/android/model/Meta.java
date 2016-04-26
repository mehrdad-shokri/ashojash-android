package com.ashojash.android.model;

import com.google.gson.annotations.SerializedName;

public class Meta {

    public Pagination pagination;

    public class Pagination {
        protected int total;
        public int count;
        @SerializedName("per_page")
        public int perPage;
        @SerializedName("current_page")
        public int currentPage;
        @SerializedName("total_pages")
        public int totalPages;
        private Links links;

        public String next() {
            return links.next;
        }

        public String previous() {
            return links.previous;
        }

        private class Links {
            public String next;
            public String previous;
        }
    }
}
