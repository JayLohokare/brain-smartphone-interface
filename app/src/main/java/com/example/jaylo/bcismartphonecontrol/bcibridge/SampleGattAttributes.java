package com.example.jaylo.bcismartphonecontrol.bcibridge;

/**
 * Created by jaylo on 05-03-2018.
 */
import java.util.HashMap;
import java.util.UUID;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();

    //Standard services that may be used to see if other BLEs are visible
    public final static String UUID_GENERIC_DEVICE_SERVICE = "00001800-0000-1000-8000-00805f9b34fb";
    public final static String UUID_DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    public final static String UUID_DEVICE_APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb";

    //device information service and characteristics
    public final static String UUID_DEVICE_INFORMATION_SERVICE = "0000180a-0000-1000-8000-00805f9b34fb";
    public final static String UUID_MANUFACTURER_NAME = "00002a29-0000-1000-8000-00805f9b34fb";
    public final static String UUID_MODEL_NUMBER = "00002a24-0000-1000-8000-00805f9b34fb";

    //additional one characteristic
    public final static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    //standard characteristics
    public final static String UUID_HEART_RATE_SERVICE = "0000180d-0000-1000-8000-00805f9b34fb";
    public final static String UUID_HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";


    public final static String UUID_BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public final static String UUID_BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";

    //Ganglion Service/Characteristics UUIDs (SIMBLEE Chip Defaults)
    public final static String UUID_GANGLION_SERVICE = "0000fe84-0000-1000-8000-00805f9b34fb";
    public final static String UUID_GANGLION_RECEIVE = "2d30c082-f39f-4ce6-923f-3484ea480596";
    public final static String UUID_GANGLION_SEND = "2d30c083-f39f-4ce6-923f-3484ea480596";
    public final static String UUID_GANGLION_DISCONNECT = "2d30c084-f39f-4ce6-923f-3484ea480596";


    //Cyton Service/Characteristics UUIDs (RFDuino Chip Defaults)
    public final static String UUID_CYTON_SERVICE = "00002220-0000-1000-8000-00805f9b34fb";
    public final static String UUID_CYTON_RECEIVE = "00002221-0000-1000-8000-00805f9b34fb";
    public final static String UUID_CYTON_SEND = "00002222-0000-1000-8000-00805f9b34fb";
    public final static String UUID_CYTON_DISCONNECT = "00002223-0000-1000-8000-00805f9b34fb";

    //Device names
    public final static String DEVICE_NAME_GANGLION = "GANGLION";
    public final static String DEVICE_NAME_CYTON = "CYTON";

    static {
        // Sample Services.
        attributes.put(UUID_GANGLION_SERVICE,    "Ganglion Service (via SIMBLEE)");
        attributes.put(UUID_GANGLION_RECEIVE,    "Ganglion Receive");
        attributes.put(UUID_GANGLION_SEND,       "Ganglion Send");
        attributes.put(UUID_GANGLION_DISCONNECT, "Ganglion Disconnect");

        attributes.put(UUID_CYTON_SERVICE,    "Cyton Service (via RFDuino)");
        attributes.put(UUID_CYTON_RECEIVE,    "Cyton Receive");
        attributes.put(UUID_CYTON_SEND,       "Cyton Send");
        attributes.put(UUID_CYTON_DISCONNECT, "Cyton Disconnect");

        attributes.put(UUID_HEART_RATE_SERVICE, "Heart Rate Service");
        attributes.put(UUID_HEART_RATE_MEASUREMENT, "Heart Rate Measurement");

        attributes.put(UUID_BATTERY_SERVICE, "Battery State Service");
        attributes.put(UUID_BATTERY_LEVEL, "Battery Level");

        attributes.put(UUID_GENERIC_DEVICE_SERVICE, "Generic Access Service");
        attributes.put(UUID_DEVICE_NAME, "Device Name");
        attributes.put(UUID_DEVICE_APPEARANCE,"Appearance");

        attributes.put(UUID_DEVICE_INFORMATION_SERVICE,"Device Information Service");
        attributes.put(UUID_MANUFACTURER_NAME, "Manufacturer Name");
        attributes.put(UUID_MODEL_NUMBER,"Model Number");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    public static String getShortUUID(UUID uuid){
        if(uuid.toString().contains("0000-1000-8000-00805f9b34fb")){
            return uuid.toString().substring(0,7);
        }
        else
            return uuid.toString().substring(0,7)+"...";
    }
}