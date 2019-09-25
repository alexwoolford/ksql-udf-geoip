package com.github.cjmatta.kafka.ksql.udfs;

import com.google.gson.JsonObject;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.record.Location;
import io.confluent.common.Configurable;
import io.confluent.common.config.ConfigException;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import io.confluent.ksql.util.KsqlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

@UdfDescription(
        name = "getgeoforip",
        description = "Function to lookup ip -> geo information ")
public class GetGeoForIP implements Configurable {

    private DatabaseReader reader;
    private Logger log = LoggerFactory.getLogger(GetGeoForIP.class);

    @Override
    public void configure(final Map<String, ?> props) {

        log.info("Configure run");

        String KSQL_FUNCTIONS_GETGEOFORIP_GEOCITY_DB_PATH_CONFIG = KsqlConfig.KSQL_FUNCTIONS_PROPERTY_PREFIX + "getgeoforip.geocity.db.path";

        if (!props.containsKey(KSQL_FUNCTIONS_GETGEOFORIP_GEOCITY_DB_PATH_CONFIG)) {
            throw new ConfigException("Required property " + KSQL_FUNCTIONS_GETGEOFORIP_GEOCITY_DB_PATH_CONFIG + " not found!");
        }

        File database;
        final String geoCityDbPath = (String) props.get(KSQL_FUNCTIONS_GETGEOFORIP_GEOCITY_DB_PATH_CONFIG);

        try {
            database = new File(geoCityDbPath);
            reader = new DatabaseReader.Builder(database).build();
            log.info("loaded GeoIP database from " + geoCityDbPath.toString());
        } catch (IOException e) {
            log.error("Problem loading GeoIP database: " + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    @Udf(description = "returns lat/lon from IP input")
    public String getgeoforip(
            @UdfParameter(value = "ip", description = "the IP address to lookup in the geoip database") String ip
    ) {

        log.info("**** in UDF ****");

        if (ip == null || ip.equals("")) {
            return null;
        }

        try {
            log.debug("Lookup lat/lon for IP: " + ip);
            InetAddress ipAddress = InetAddress.getByName(ip);
            Location response = reader.city(ipAddress).getLocation();

            JsonObject location = new JsonObject();
            location.addProperty("lat", reader.city(ipAddress).getLocation().getLatitude());
            location.addProperty("lon", reader.city(ipAddress).getLocation().getLongitude());

            JsonObject result = new JsonObject();
            result.add("location", location);

            return result.toString();
        } catch (IOException | GeoIp2Exception e) {
            log.error("Error looking up lat/lon for IP: " + e);
            return null;
        }
    }

}
