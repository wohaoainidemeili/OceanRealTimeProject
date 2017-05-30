package yuan.ocean.InsertObservationService.InsertOper;

import yuan.ocean.Entity.Sensor;
import yuan.ocean.Entity.Station;

import java.util.List;
import java.util.Map;

/**
 * Created by Yuan on 2017/5/15.
 */
public interface IInsertOper {
    //insert sensor info
    public void Insert(Station station, List jsonData, Map<String, String> propertyPattern);

    //get time by regex and cast it to time pattern
    public String getTime(String timeStr);

}
