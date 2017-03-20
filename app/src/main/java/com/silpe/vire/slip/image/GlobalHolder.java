package com.silpe.vire.slip.image;

class GlobalHolder {

    private PickerManager pickerManager;

    private static GlobalHolder ourInstance = new GlobalHolder();

    public static GlobalHolder getInstance() {
        return ourInstance;
    }

    private GlobalHolder() {
    }


    PickerManager getPickerManager() {
        return pickerManager;
    }

    void setPickerManager(PickerManager pickerManager) {
        this.pickerManager = pickerManager;
    }
}
