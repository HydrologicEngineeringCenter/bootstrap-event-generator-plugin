package usace.hec.model.timeseries;

import usace.cc.plugin.api.DataStore;
import usace.cc.plugin.api.eventstore.Recordset;

public class TimeSeries {
    public TimeSeriesRecord[] timeSeries;
    public String storagePath;
    public TimeSeries(){}
    public TimeSeries(String path, TimeSeriesRecord[] records){
        storagePath = path;
        timeSeries = records;
    }
    public void Write(DataStore ds){
        Recordset<TimeSeriesRecord> rs = new Recordset<>(ds,storagePath);//@TODO: determine how to handle datastores elegantly.
        try {
            rs.create(timeSeries);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static TimeSeries ReadAll(DataStore ds, String path){
        Recordset<TimeSeriesRecord> rs = new Recordset<>(ds,path);//@TODO: determine how to handle datastores elegantly.
        try {
            TimeSeriesRecord[] out = rs.read(TimeSeriesRecord.class);
            return new TimeSeries(path,out);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new TimeSeries();
        }
    }
}
