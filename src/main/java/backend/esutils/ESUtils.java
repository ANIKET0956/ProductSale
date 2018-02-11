package backend.esutils;

import backend.ServerConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.omg.CORBA.MARSHAL;

public class ESUtils {

    private static ESClient DEFAULT_CLIENT;
    private static ObjectMapper MAPPER_INSTANCE = new ObjectMapper();

    static {
        try {
            DEFAULT_CLIENT = new ESClientBuilder().addServerDetails(ServerConfig.getDefaultESServerConfig()).build();
        } catch (Exception ex) {
            System.out.println("Error while creating ES Client"+ ex);
        }

    }

    public static ESClient getDefaultInstance() {
        return DEFAULT_CLIENT;
    }

    public static ObjectMapper getMapperInstance() { return MAPPER_INSTANCE; }


}
