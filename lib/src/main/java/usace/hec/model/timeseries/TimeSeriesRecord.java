package usace.hec.model.timeseries;

import java.time.ZonedDateTime;

import usace.cc.plugin.api.eventstore.Recordset.EventStoreAttr;

public class TimeSeriesRecord {
    @EventStoreAttr("time")
    public String Time;
    @EventStoreAttr("value")
    public Double Value;
    public TimeSeriesRecord(){}
    public TimeSeriesRecord(String time, Double value){
        Time = time;
        Value = value;
    }
}
