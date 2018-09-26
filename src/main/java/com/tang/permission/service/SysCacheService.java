package com.tang.permission.service;

import com.tang.permission.bean.CachKeyConstans;

public interface SysCacheService {

    void saveCache(String toSaveValue, int timeoutSeconds, CachKeyConstans perfix);

    void saveCache(String toSaveValue, int timeoutSeconds, CachKeyConstans perfix, String... keys);

    public String getFromCache(CachKeyConstans prefix, String... keys);
}
