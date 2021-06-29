package utils;

/**
 * wgs84坐标系：即地球坐标系，国际上通用的坐标系。
 * gcj02坐标系：即火星坐标系，是由中国国家测绘局制订的地理信息系统的坐标系统。
 */
public class PositionUtil {
    
	private static final String BAIDU_LBS_TYPE = "bd09ll";
    
    private static double pi = 3.1415926535897932384626;
    private static double a = 6378245.0;
    private static double ee = 0.00669342162296594323;
    
    public static final int W2G=1;//wgs84 to gcj02
    
    public static final int G2W=2;//gcj02 to wgs84
    
    public static final int W2B=3;//wgs84 to bd09
    
    public static final int B2W=4;//BD09 to wgs84
    public static final int NoGB=0; //无偏移
    /**
     * wgs84坐标系转 gcj02
     * 
     * @param lon 经度
     * @param lat 纬度
     * @return Location
     */
    public static Location wgs84_To_gcj02(double lon, double lat) {
        if (outOfChina(lon,lat)) {
            return null;
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new Location(mgLon,mgLat);
    }

    /**
     **gcj02 转 wgs84  
     *  @param lon 经度  
     *  @param lat 纬度
     *  @return the result of Location 
     * */
    public static Location gcj02_To_wgs84(double lon, double lat) {
        Location gps = transform(lon,lat);
        double lontitude = lon * 2 - gps.getLon();
        double latitude = lat * 2 - gps.getLat();
        return new Location(lontitude,latitude);
    }

    /**
     * gcj02转bd09坐标系
     * @param gg_lon 经度
     * @param gg_lat 纬度
     * @return Location
     */
    public static Location gcj02_To_bd09(double gg_lon, double gg_lat) {
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * pi);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new Location(bd_lon,bd_lat);
    }

    /**
     * bd09转gcj02坐标系
     * @param bd_lon 经度
     * @param bd_lat 纬度
     * @return Location
     */
    public static Location bd09_To_gcj02(double bd_lon, double bd_lat) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new Location(gg_lon,gg_lat);
    }

    /**
     * bd09转wgs84坐标系
     * @param bd_lon 经度
     * @param bd_lat 纬度
     * @return Location
     */
    public static Location bd09_To_wgs84(double bd_lon, double bd_lat) {

        Location gcj02 = PositionUtil.bd09_To_gcj02(bd_lon,bd_lat);
        Location map84 = PositionUtil.gcj02_To_wgs84( gcj02.getLon(),gcj02.getLat());
        return map84;

    }
    /**
     * wgs84转bd09坐标系
     * @param lon
     * @param lat
     * @return Location
     */
    public static Location wgs84_To_bd09(double lon,double lat) {
    	Location gcj02=PositionUtil.wgs84_To_gcj02(lon, lat);
    	Location bd09 =PositionUtil.gcj02_To_bd09(gcj02.getLon(), gcj02.getLat());
    	return bd09;
    }
    /**
     **判断是否在中国范围内
     * @param lon 经度
     * @param lat 纬度
     * @return boolean 
     */
    public static boolean outOfChina(double lon, double lat) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }
    /**
     * 
     * @param lon lon
     * @param lat lat
     * @return Location
     */
    private static Location transform(double lon, double lat) {
        if (outOfChina(lon,lat)) {
            return new Location(lon,lat);
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new Location(mgLon,mgLat);
    }
    /**
     * 
     * @param x x
     * @param y y
     * @return double result
     */
    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }
    /**
     * 
     * @param x x
     * @param y y
     * @return double 
     */
    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }

    public static void main(String[] args) {

        // 北斗芯片获取的经纬度为WGS84地理坐标 31.426896,119.496145
        Location gps = new Location(100.30830383,30.94067955);
//        System.out.println("gps :" + gps);
        Location gcj = wgs84_To_gcj02(gps.getLon(),gps.getLat());
        System.out.println("gcj :" + gcj);
        Location wgs84 =gcj02_To_wgs84(100.30960656823223,30.938250541999903);
        System.out.println("wgs84 :" + wgs84);
        Location wgs2bd=wgs84_To_bd09(103.456,30.456);
        System.out.println("bd02="+wgs2bd);
    }
}