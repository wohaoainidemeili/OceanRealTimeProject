package yuan.ocean.InsertObservationService;

import org.apache.xmlbeans.XmlException;
import yuan.ocean.Entity.FuZhouStationJsonDesc;
import yuan.ocean.Entity.Sensor;
import yuan.ocean.Entity.Station;
import yuan.ocean.SensorConfigInfo;
import yuan.ocean.Util.Decode;
import yuan.ocean.Util.Encode;
import yuan.ocean.Util.HttpRequestAndPost;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * Created by Yuan on 2017/5/12.
 */
public class InsertObservationThread implements Runnable {
    List<FuZhouStationJsonDesc> jsonDescs;
    String className;
    Map<String,String> linkedProperty;
    Station sensor;
    public InsertObservationThread(List<FuZhouStationJsonDesc> jsonDescs,String stationID,String className,Map<String,String> linkedProperty){
        this.jsonDescs=jsonDescs;
        this.className=className;
        this.linkedProperty=linkedProperty;
        //get this sensor using sensorML
        try {
            this.sensor= Decode.parseSensorML(stationID);
        } catch (XmlException e) {
            System.out.println(stationID);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(stationID);
            e.printStackTrace();
        }
    }
    public void run() {
        if (sensor!=null) {
            //insert Observation invoke method
            Class insertClass = null;
            try {
                insertClass = Class.forName(className);
                Object object = insertClass.newInstance();
                Method method = insertClass.getMethod("Insert", Station.class, List.class, Map.class);
                method.invoke(object, sensor, jsonDescs, linkedProperty);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
