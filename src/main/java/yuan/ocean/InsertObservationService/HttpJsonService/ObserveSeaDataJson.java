package yuan.ocean.InsertObservationService.HttpJsonService;

import com.alibaba.fastjson.JSON;
import yuan.ocean.Entity.FuZhouStationJsonDesc;
import yuan.ocean.InsertObservationService.IUtilService;
import yuan.ocean.Util.HttpRequestAndPost;

import java.util.List;

/**
 * Created by Yuan on 2017/5/26.
 */
public class ObserveSeaDataJson implements IUtilService {

    public List getJsonList(String urlStr) {
        String dataStr= HttpRequestAndPost.getWebPageInfo(urlStr);
        return JSON.parseArray(dataStr).toJavaList(FuZhouStationJsonDesc.class);
    }
}
