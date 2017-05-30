package yuan.ocean.InsertObservationService.InsertOper;

import yuan.ocean.Entity.*;
import yuan.ocean.SensorConfigInfo;
import yuan.ocean.Util.Encode;
import yuan.ocean.Util.HttpRequestAndPost;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuan on 2017/5/27.
 */
public class fujianInsert implements IInsertOper {
    public void Insert(Station station, List jsonDatas, Map<String, String> propertyPattern) {
        //insertData
        //get the Class of property for geting the propertyvalue with invoke method
        Class fuzhouJsonClass=null;
        try {
           fuzhouJsonClass=Class.forName("yuan.ocean.Entity.FuZhouStationJsonDesc");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        for (Sensor sensor:station.getSensors()){
            //if all the property is in
            if (isSensorMatchProperty(propertyPattern,sensor)){
                String timePos=propertyPattern.get("time");
                for (FuZhouStationJsonDesc jsonDesc:(List<FuZhouStationJsonDesc>)jsonDatas){
                    SOSWrapper sosWrapper=new SOSWrapper();
                    sosWrapper.setSensorID(sensor.getSensorID());
                    sosWrapper.setLat(sensor.getLat());
                    sosWrapper.setLon(sensor.getLon());
                    sosWrapper.setSrid(4326);
                    String timeValue=null;
                    try {
                        Method timemethod = fuzhouJsonClass.getMethod(timePos.trim());
                        timeValue = (String)timemethod.invoke(jsonDesc);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    //if time is null ,Then break to stop insert
                    if (timeValue==null) break;
                    //translate time to simpledateformat
                    String obsTime=getTime(timeValue);
                    sosWrapper.setSimpleTime(obsTime);
                    for (ObservedProperty property:sensor.getObservedProperties()) {
                        try {
                            Method method = fuzhouJsonClass.getMethod(propertyPattern.get(property.getPropertyID()).trim());
                            String propertyValue= (String) method.invoke(jsonDesc);
                            //if there is no data then cover it to -32768
                            if (propertyValue==null) propertyValue="-32768";
                            //using wind speed to get the windlevel
                            if (property.getPropertyID().equals("urn:ogc:def:property:OGC:1.0:windLevel")){
                               propertyValue=getWindLevel(propertyValue);
                            }
                            //set data value
                            property.setDataValue(propertyValue);
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    sosWrapper.setProperties(sensor.getObservedProperties());
                    //register the sosWrapper data into SOS
                    String insertXML= Encode.getInserObservationXML(sosWrapper);
                    String responseXML=  HttpRequestAndPost.sendPost(SensorConfigInfo.getUrl(), insertXML);
                    System.out.println(responseXML);
                }
            }
        }
    }

    public String getTime(String timeStr) {
        String resultTimeStr=null;
        timeStr="2017年"+timeStr;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年MM'月'dd'日'HH'时'mm'分'");
        SimpleDateFormat resultDateFormat=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date date=simpleDateFormat.parse(timeStr);
           resultTimeStr=resultDateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultTimeStr;
    }
    public boolean isSensorMatchProperty(Map<String,String> linkedProperty,Sensor sensor){
        boolean isContain=true;
        for (ObservedProperty property:sensor.getObservedProperties()){
            if (!linkedProperty.containsKey(property.getPropertyID()))
                isContain=false;
        }
        return isContain;
    }

    public String getWindLevel(String windSpeed){
        double windSpeedDouble=Double.valueOf(windSpeed);
        if (windSpeedDouble<=0.2&&windSpeedDouble>=0)
            return "0";
        else if (windSpeedDouble<=1.5)
            return "1";
        else if (windSpeedDouble<=3.3)
            return "2";
        else if (windSpeedDouble<=5.4)
            return "3";
        else if (windSpeedDouble<=7.9)
            return "4";
        else if (windSpeedDouble<10.7)
            return "5";
        else if (windSpeedDouble<=13.8)
            return "6";
        else if (windSpeedDouble<=17.1)
            return "7";
        else if (windSpeedDouble<=20.7)
            return "8";
        else if (windSpeedDouble<=24.4)
            return "9";
        else if (windSpeedDouble<=28.4)
            return "10";
        else if (windSpeedDouble<=32.6)
            return "11";
        else if (windSpeedDouble<=36.9)
            return "12";
        return "0";
    }
}
