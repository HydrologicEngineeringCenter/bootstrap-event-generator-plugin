package usace.hec.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;

import usace.cc.plugin.api.ConnectionDataStore;
import usace.cc.plugin.api.DataStore;
import usace.cc.plugin.api.EnvironmentVariables;
import usace.cc.plugin.api.eventstore.ArrayResult;
import usace.cc.plugin.api.eventstore.EventStore;
import usace.cc.plugin.api.eventstore.EventStore.ArrayAttribute;
import usace.cc.plugin.api.eventstore.EventStore.CreateArrayInput;
import usace.cc.plugin.api.eventstore.EventStore.GetArrayInput;
import usace.cc.plugin.api.eventstore.EventStore.PutArrayBuffer;
import usace.cc.plugin.api.eventstore.EventStore.PutArrayInput;
import usace.cc.plugin.api.eventstore.MultiDimensionalArrayStore;

public class CsvEventStore implements ConnectionDataStore, MultiDimensionalArrayStore, AutoCloseable {
    private String basePath;
    private static final String ROOT_PARAM = "root";
    @Override
    public void close() throws Exception {
        return;
    }

    @Override
    public void createArray(CreateArrayInput cai) throws Exception {
        Path fullPath = Paths.get(basePath).resolve(cai.arrayPath);
        try {
            if (Files.exists(fullPath)) {
                Files.delete(fullPath);//TODO: remove this line or change behavior to append.
                //throw new Exception("Array already existed at: " + fullPath);
            }
            // Create parent directories if needed
            Files.createDirectories(fullPath.getParent());
            String hdr = "";
            for (ArrayAttribute attribute : cai.attributes) {
                if(hdr==""){
                    hdr = attribute.name;
                }else{
                    hdr += "," + attribute.name;
                }
            }
            hdr += "\n";
            Files.writeString(fullPath, hdr, StandardOpenOption.CREATE);//StandardOpenOption.SPARSE
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public ArrayResult getArray(GetArrayInput gai) throws Exception {
       //System.out.print(gai);
       ArrayResult ar = new ArrayResult();
       long firstRow = gai.bufferRange[0];
       long lastRow = gai.bufferRange[1];
       Path fullPath = Paths.get(basePath).resolve(gai.arrayPath);
       List<String> lines = Files.readAllLines(fullPath);
        long idx = 0;
        Object[] buffer = new Object[gai.attrs.length];
        long[][] offsets = new long[gai.attrs.length][(int)lastRow-(int)firstRow+1];
        for(int column = 0; column<gai.attrs.length; column++){
            Object columnData = null;
            boolean isDouble = true;
        for (String string : lines) {

                if (idx >= firstRow){
                    String value = string.split(",")[column];
                    try{
                        Double dblvalue = Double.parseDouble(value);
                        columnData = new Double[(int)lastRow-(int)firstRow+1];//fenceposts are weird
                    }catch(NumberFormatException ex){
                        isDouble = false;
                        columnData = new String();
                    }
                    
                    if(idx<=lastRow){
                        //retrieve data.
                        String rowcolvalue = string.split(",")[column];
                        if(isDouble){
                            try{
                                Double cellvalue = Double.parseDouble(rowcolvalue);
                                ((Double[])columnData)[(int)idx-1]=cellvalue;
                            }catch(NumberFormatException ex){
                                isDouble = false;
                            }   
                        }else{
                            offsets[column][(int)idx-1] = (long)((String)columnData).length();
                            columnData += rowcolvalue;
                        }

                    }
                }
                idx ++;
        }  
        buffer[column] = columnData;          
        }
        ar.buffers = buffer;
        ar.offsetBuffers = offsets;
       //read in the csv, parse it return objects for the buffers (Double[] and String with offsets.)
       return ar;
    }

    @Override
    public void putArray(PutArrayInput pai) throws Exception {
        //buffer range is the range in the rows 1 based index not 0 based...
        long nrows = pai.bufferRange[1];//expected last rows being put in the case of a record set...
        if(pai.putLayout!=EventStore.LayoutOrder.ROWMAJOR){throw new Exception("do not support column layout");}
        Object[][] data = new Object[pai.buffers.length][(int) nrows];
        int row = 0;
        for (PutArrayBuffer pab : pai.buffers) {
            System.out.println(pab.attrName);
            System.out.println(pab.buffer);
            if(pab.buffer.getClass()==String.class){
                String[] strings = new String[(int)nrows];
                String strdata = (String)pab.buffer;
                for(int i =0; i < pab.offsets.length-1;i++){
                    strings[i] = strdata.substring((int)pab.offsets[i], (int)pab.offsets[i+1]); 
                }
                strings[pab.offsets.length-1] = strdata.substring((int)pab.offsets[pab.offsets.length-1]);
                data[row] =strings;
            }else{
                data[row] = (Object[])pab.buffer;
            }
            row ++;
        }
        //write it.
        Path fullPath = Paths.get(basePath).resolve(pai.arrayPath);
        String strData = "";
        for(int r=0; r < nrows;r++){
            for(int col = 0; col<pai.buffers.length;col++){
                strData += data[col][r].toString() + ",";
            }
            strData = strData.substring(0,strData.length()-1) + "\n";
        }
        strData = strData.substring(0,strData.length()-1);
        Files.writeString(fullPath, strData, StandardOpenOption.APPEND);
    }

    @Override
    public ConnectionDataStore connect(DataStore ds) throws FailedToConnectError {
        String rootPath = System.getenv(EnvironmentVariables.FSB_ROOT_PATH);
        if (rootPath == null || rootPath.isEmpty()) {
            throw new FailedToConnectError(new IllegalArgumentException(
                "FSB_ROOT_PATH environment variable is required for filesystem storage"));
        }

        // Get the "root" parameter from DataStore params (subdirectory)
        String subPath = null;
        try {
            Optional<String> optParam = ds.getParameters().get(ROOT_PARAM);
            if (optParam.isPresent()) {
                subPath = optParam.get();
            }
        } catch (Exception e) {
            throw new FailedToConnectError(e);
        }

        // Build the full base path
        Path basePath = Paths.get(rootPath);
        if (subPath != null && !subPath.isEmpty()) {
            if (subPath.startsWith("/")) {
                // Absolute path
                basePath = Paths.get(subPath);
            } else {
                // Relative to rootPath
                basePath = basePath.resolve(subPath);
            }
        }
        this.basePath = basePath.toString();

        // Ensure directory exists
        try {
            Files.createDirectories(basePath);
        } catch (IOException e) {
            throw new FailedToConnectError(new Exception("Failed to create directory: " + this.basePath, e));
        }

        return this;
    }

    @Override
    public Object rawSession() {
        //seession has to implement the Multidimensional array store
        return this;
    }
    
}
