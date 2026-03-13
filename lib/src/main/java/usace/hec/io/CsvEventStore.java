package usace.hec.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import usace.cc.plugin.api.ConnectionDataStore;
import usace.cc.plugin.api.DataStore;
import usace.cc.plugin.api.EnvironmentVariables;
import usace.cc.plugin.api.eventstore.ArrayResult;
import usace.cc.plugin.api.eventstore.EventStore.ArrayAttribute;
import usace.cc.plugin.api.eventstore.EventStore.CreateArrayInput;
import usace.cc.plugin.api.eventstore.EventStore.GetArrayInput;
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
                throw new Exception("Array already exists at: " + fullPath);
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
       System.out.print(gai);
       return null;
    }

    @Override
    public void putArray(PutArrayInput pai) throws Exception {
        long buffersize = pai.bufferRange[1];//expected count of rows being put in the case of a record set...
        System.out.print(buffersize);
        System.out.print(pai);
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
        return basePath;
    }
    
}
