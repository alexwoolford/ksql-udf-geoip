package com.github.cjmatta.kafka.ksql.udfs;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.DatabaseReader.Builder;
import com.maxmind.geoip2.record.Location;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

import org.apache.kafka.common.Configurable;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UdfDescription(
        name = "getgeoforip",
        description = "Function to lookup ip -> geo information "
)
public class GeoipLookup implements Configurable {
    private DatabaseReader reader;
    private Logger log = LoggerFactory.getLogger(GeoipLookup.class);

    public void configure(Map<String, ?> props) {
        String KSQL_FUNCTIONS_GETGEOFORIP_GEOCITY_DB_PATH_CONFIG = "ksql.functions.getgeoforip.geocity.db.path";
        if (!props.containsKey(KSQL_FUNCTIONS_GETGEOFORIP_GEOCITY_DB_PATH_CONFIG)) {
            throw new ConfigException("Required property " + KSQL_FUNCTIONS_GETGEOFORIP_GEOCITY_DB_PATH_CONFIG + " not found!");
        } else {
            String geoCityDbPath = (String) props.get(KSQL_FUNCTIONS_GETGEOFORIP_GEOCITY_DB_PATH_CONFIG);

            try {
                File database = new File(geoCityDbPath);
                this.reader = (new Builder(database)).build();
                this.log.info("Loaded GeoIP database from " + geoCityDbPath);
            } catch (IOException e) {
                this.log.error("Problem loading GeoIP database: " + e);
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    @Udf(description = "returns geolocation for an IP address",
            schema = "STRUCT<CITY VARCHAR, COUNTRY VARCHAR, SUBDIVISION VARCHAR, LOCATION STRUCT<LON DOUBLE, LAT DOUBLE>>")

    public Struct getgeoforip(@UdfParameter(
            value = "ip",
            description = "the IP address to lookup in the geoip database") String ip) {

        String city;
        try {
            city = this.reader.city(InetAddress.getByName(ip)).getCity().getName();
        } catch (Exception e) {
            city = null;
        }

        String country;
        try {
            country = this.reader.city(InetAddress.getByName(ip)).getCountry().getName();
        } catch (Exception e) {
            country = null;
        }

        String subdivision;
        try {
            subdivision = this.reader.city(InetAddress.getByName(ip)).getSubdivisions().get(0).getName();
        } catch (Exception e) {
            subdivision = null;
        }

        Location location;
        try {
            location = this.reader.city(InetAddress.getByName(ip)).getLocation();
        } catch (Exception e) {
            location = null;
        }

        GeoSchema geoSchema = new GeoSchema();
        Struct geolocation = new Struct(geoSchema.getGeoipLocationSchema())
                .put("CITY", city)
                .put("COUNTRY", country)
                .put("SUBDIVISION", subdivision);

        Struct latlon;
        if (location != null) {
            latlon = new Struct(geoSchema.getLatLonSchema())
                    .put("LON", location.getLongitude())
                    .put("LAT", location.getLatitude());
        } else {
            latlon = new Struct(geoSchema.getLatLonSchema())
                    .put("LON", null)
                    .put("LAT", null);
        }
        geolocation.put("LOCATION", latlon);

        return geolocation;
    }

}
