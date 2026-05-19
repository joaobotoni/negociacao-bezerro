    package com.omni.negociacaobezerros.utils.pdf;


    public interface PageAware {
        void setPageInfo(int currentPage, int totalPages);
    }