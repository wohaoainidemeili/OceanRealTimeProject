import yuan.ocean.InsertObservationService.InsertOper.fujianInsert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yuan on 2017/5/27.
 */
public class chineseTest {
    public static void main(String[] args){
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM'月'dd'日'HH'时'mm'分'");
        try {
            Date date=simpleDateFormat.parse("05月27日09时30分");
            fujianInsert fujianInsert=new fujianInsert();
            String re =fujianInsert.getTime("05月27日09时30分");

            int x=0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("中文");
    }
}
