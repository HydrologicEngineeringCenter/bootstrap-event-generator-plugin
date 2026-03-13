package usace.hec.model.timeseries;

import java.time.ZonedDateTime;

import usace.cc.plugin.api.eventstore.Recordset.EventStoreAttr;

public class TimeSeriesRecord {
    @EventStoreAttr("time")
    public ZonedDateTime Time;
    @EventStoreAttr("value")
    public Double Value;
    public TimeSeriesRecord(){}
    public TimeSeriesRecord(ZonedDateTime time, Double value){
        Time = time;
        Value = value;
    }
}
