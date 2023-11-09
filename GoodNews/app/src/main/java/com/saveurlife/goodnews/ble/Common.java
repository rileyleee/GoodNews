package com.saveurlife.goodnews.ble;

import java.util.UUID;

public class Common {
    public static final UUID SERVICE_UUID = UUID.fromString("769a255e-35f4-4558-be43-ce0c69626bc4");
    public static final UUID DEVICEINFO_UUID = UUID.fromString("57e0127f-8374-40be-ac52-5828731ab4fc");
    public static final UUID CHARACTERISTIC_UUID = UUID.fromString("f65e282a-85f1-4750-af15-8d50c77a0ad8");
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    public static final int PERMISSION_REQUEST_CODE = 1001;
}
