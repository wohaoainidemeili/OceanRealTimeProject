package yuan.ocean.InsertObservationService;

import yuan.ocean.Entity.FuZhouStationJsonDesc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Created by Yuan on 2017/5/12.
 */
public class InsertObservationTaskThread implements Runnable {
    private String resourceFtpUrl;
    //class name for decode url
    private String insertClassName;
    private String downloadClassName;
    //decode the stationIDs.csv to get all the stationIDs
    private Map<String,String> stationIDs=new HashMap<String, String>();
    //the linkedProperty using regex to select the data url
    private Map<String,String> linkedProperty=new HashMap<String, String>();
    private ExecutorService executorService= Executors.newFixedThreadPool(3);
    public InsertObservationTaskThread(String resourceFtpUrl,String stationIDFile,String linkedFile,String className,String downloadClassName){
        this.resourceFtpUrl=resourceFtpUrl;
        this.insertClassName=className;
        this.downloadClassName=downloadClassName;
        //get stationIDs
        //read IDs
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(stationIDFile)));
        String tempID=null;
        try {
            while ((tempID=bufferedReader.readLine())!=null){
                String[] eles=tempID.split(",");
                stationIDs.put(eles[0],eles[1]);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //read linked properties and store it in hashmap
        BufferedReader bufferedReader1=new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(linkedFile)));
        try {
            while ((tempID=bufferedReader1.readLine())!=null){
                String[] eles=tempID.split(",");
                linkedProperty.put(eles[0],eles[1]);
            }
            bufferedReader1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void run() {
        //get jsonData
        List<FuZhouStationJsonDesc> jsonDatas=null;
        try {
            Class downloadClass=Class.forName(downloadClassName);
            Object objectDownload=downloadClass.newInstance();
            Method methodDownload= downloadClass.getMethod("getJsonList", String.class);
            jsonDatas=(List)methodDownload.invoke(objectDownload,resourceFtpUrl);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        // List<String> urlPaths=  FTPUtil.getFileListUrl(resourceFtpUrl, parentFilePath, userName, passWord);


        //using regex to split different
        for (Map.Entry entry:stationIDs.entrySet()) {
            List<FuZhouStationJsonDesc> stationJsonDescs=new ArrayList<FuZhouStationJsonDesc>();
            for (FuZhouStationJsonDesc jsonData:jsonDatas) {
                if (jsonData.getStationName().equals(entry.getValue()))
                    stationJsonDescs.add(jsonData);
            }
            if (stationJsonDescs.size()>0) {
                InsertObservationThread insertObservationThread = new InsertObservationThread(stationJsonDescs, (String) entry.getKey(), insertClassName, linkedProperty);
                if (!executorService.isShutdown())
                    executorService.execute(insertObservationThread);
            }

        }

    }
}
