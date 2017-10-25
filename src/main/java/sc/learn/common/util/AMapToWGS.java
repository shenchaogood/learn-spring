package sc.learn.common.util;

/**
 * 高德使用GCJ-02坐标系统
 */
public class AMapToWGS {
	
	public static class LatLonPoint{
		private double latitude;
		private double longitude;
		public LatLonPoint(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}
		public double getLatitude() {
			return latitude;
		}
		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}
		public double getLongitude() {
			return longitude;
		}
		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}
		@Override
		public String toString() {
			return "LatLonPoint [latitude=" + latitude + ", longitude=" + longitude + "]";
		}
	}

    private final static double a=6378245.0;
//    private final static double Math.PI=3.14159265358979324;
    private final static double ee=0.00669342162296594626;
    
    public static void main(String[] args) {
		System.out.println(toWGS84Point(116.487585177952,39.991754014757));
	}

    //gcj-02  to  wgs-84
    public static LatLonPoint toWGS84Point(double latitude,double longitude){
        LatLonPoint dev=calDev(latitude, longitude);
        double retLat = latitude-dev.getLatitude();
        double retLon=longitude-dev.getLongitude();
        dev=calDev(retLat, retLon);
        retLat=latitude-dev.getLatitude();
        retLon=longitude-dev.getLongitude();
        return new LatLonPoint(retLat, retLon);
    }

    //wsg84 to  gcj02
    public static LatLonPoint toGCJ02Piont(double latitude,double longitude){
        LatLonPoint dev=calDev(latitude, longitude);
        double retLat = latitude-dev.getLatitude();
        double retLon=longitude-dev.getLongitude();
        return new LatLonPoint(retLat, retLon);
    }

    private static LatLonPoint calDev(double wgLat,double wgLon){
//        if(isOutofChina(wgLat,wgLon)){
//            return new LatLonPoint(0,0);
//        }
        double dLat=calLat(wgLon-105.0,wgLat-35.0);
        double dLon=calLon(wgLon-105.0, wgLat-35.0);
        double radLat=wgLat/180.0*Math.PI;
        double magic=Math.sin(radLat);
        magic=1-ee*magic*magic;
        double sqrtMagic=Math.sqrt(magic);
        dLat=(dLat*180.0)/((a*(1-ee))/(magic*sqrtMagic)*Math.PI);
        dLon=(dLon*180.0)/(a/sqrtMagic*Math.cos(radLat)*Math.PI);
        return new LatLonPoint(dLat,dLon);
    }

    private static double calLat(double x, double y) {
        double ret=-100.0+2.0*x+3.0*y+0.2*y*y+0.1*x*y+0.2*Math.sqrt(Math.abs(x));
        ret +=(20.0*Math.sin(6.0*x*Math.PI)+20.0*Math.sin(2.0*x*Math.PI))*2.0/3.0;
        ret +=(20.0*Math.sin(y*Math.PI)+40.0*Math.sin(y/3.0*Math.PI))*2.0/3.0;
        ret +=(160.0*Math.sin(y/12.0*Math.PI)+320*Math.sin(y*Math.PI/30.0))*2.0/3.0;

        return ret;
    }

    private static double calLon(double x,double y){
        double ret=300.0+x+2.0*y+0.1*x*x+0.1*x*y+0.1*Math.sqrt(Math.abs(x));
        ret +=(20.0*Math.sin(6.0*x*Math.PI)+20.0*Math.sin(2.0*x*Math.PI))*2.0/3.0;
        ret +=(20.0*Math.sin(x*Math.PI)+40.0*Math.sin(x/3.0*Math.PI))*2.0/3.0;
        ret +=(150.0*Math.sin(x/12.0*Math.PI)+300.0*Math.sin(x/30.0*Math.PI))*2.0/3.0;;
        return ret;

    }

    private static boolean isOutofChina(double lat, double lon) {
        if(lon<72.004 || lon>137.8347){
            return true;
        }
        if(lat<0.8293 || lat>55.8271){
            return true;
        }
        return false;
    }

}