package com.asw.billing.common;

import com.asw.billing.storage.BillingMemoryStorage;

public class Parameters {
    private String storageImplementationClassName = BillingMemoryStorage.class.getSimpleName();
    public String getStorageClassName(){
        return storageImplementationClassName;
    }
    public void setStorageClassname(String name){
        this.storageImplementationClassName = name;
    }

}
