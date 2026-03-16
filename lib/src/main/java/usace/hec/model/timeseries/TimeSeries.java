package usace.hec.model.timeseries;

import usace.cc.plugin.api.DataStore;
import usace.cc.plugin.api.eventstore.Recordset;

public class TimeSeries {
    public TimeSeriesRecord[] timeSeries;
    public String storagePath;
    Recordset<TimeSeriesRecord> rs;
    public TimeSeries(){}
    public TimeSeries(String path, TimeSeriesRecord[] records){
        storagePath = path;
        timeSeries = records;
    }
    public void Write(DataStore ds){
        rs = new Recordset<>(ds,storagePath);//@TODO: determine how to handle datastores elegantly.
        try {
            rs.create(timeSeries);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public TimeSeries Read(DataStore ds, String path, long... recrange){
        try {
            TimeSeriesRecord[] out = rs.read(TimeSeriesRecord.class, recrange);
            return new TimeSeries(path,out);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new TimeSeries();
        }
    }
}
