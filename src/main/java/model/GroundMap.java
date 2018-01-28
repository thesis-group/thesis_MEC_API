package model;

import java.util.Map;

public class GroundMap {
    // 地图 key：ZoneID value: 是否有薄云
    // 默认地图有50个zone   每个zone都有AP，但每个AP上是否有cloudlet不确定
    public static Map<Integer, Boolean> map;

    /**
     * 返回某个zone的ap上是否有cloudlet
     * @param zoneID
     * @return
     */
    public static boolean hasCloudlet(int zoneID){
        return map.get(zoneID);
    }

}
