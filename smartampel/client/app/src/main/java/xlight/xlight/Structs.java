package xlight.xlight;

public class Structs {
    
    enum LIGHT_STATES {
        IDLE,
        GREEN,
        RED,
        YELLOW,
        ALARM
    }
    
    static XLightState newXLightState() {
        return new XLightState();
    }
    
    static class XLightState {
        
        static final double NEARBY_THRESHOLD = 2.0;
        
        String uuid;
        double distance;
        int txPower;
        int rssi;
        int remoteState;
        String location;
        boolean remoteKnown;
        boolean lightNearby;
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("UUID=").append(uuid).append("\n");
            sb.append("DIST=").append(distance).append("\n");
            sb.append("TXPO=").append(txPower).append("\n");
            sb.append("RSSI=").append(rssi).append("\n");
            sb.append("NEARBY   =").append(lightNearby);
            if (lightNearby) {
                sb.append("\n");
                sb.append("REM-KNOWN=").append(remoteKnown).append("\n");
                sb.append("REM-STATE=").append(remoteState).append("\n");
                sb.append("LOCATION =").append(location).append("\n");
                sb.append("LIGHT @ ").append(LIGHT_STATES.values()[remoteState]);
            }
            return sb.toString();
        }
    }
    
}
