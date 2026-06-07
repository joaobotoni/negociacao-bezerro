package com.omni.negociacaobezerros.utils.document.pdf;


public interface PageAware {
    void setPageInfo(int currentPage, int totalPages);
}