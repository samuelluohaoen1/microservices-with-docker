package se.magnus.util.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * `@Component` indicates that an annotated class is a "component". Such classes are considered
 * as candidates for auto-detection when using annotation-based configuration and classpath scanning.
 */
@Component
public class ServiceUtil {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceUtil.class);

    private final String port;

    private String serviceAddress = null;

    /**
     * `@Autowired` enables automatic injections which injects as many suitable beans as possible
     * into the parameters. `@Value` Annotation at the field or method/constructor parameter level
     * that indicates a default value expression for the affected argument.
     *
     * @param port The port where the server is listening to.
     */
    @Autowired
    public ServiceUtil(
            @Value("${server.port}") String port) {

        this.port = port;
    }

    public String getServiceAddress() {
        if (serviceAddress == null) {
            serviceAddress = findMyHostname() + "/" + findMyIpAddress() + ":" + port;
        }
        return serviceAddress;
    }

    private String findMyHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown host name";
        }
    }

    private String findMyIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "unknown IP address";
        }
    }

}

