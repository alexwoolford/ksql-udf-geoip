package com.github.cjmatta.kafka.ksql.udfs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import org.apache.kafka.common.Configurable;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import io.confluent.ksql.util.KsqlConfig;
import org.apache.kafka.common.config.ConfigException;
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

        return geoenrich(ip);

    }

    public String geoenrich(String ip) {

        LocationRecord locationRecord = new LocationRecord();

        City city;
        try {
            city = reader.city(InetAddress.getByName(ip)).getCity();
        } catch (Exception e) {
            city = null;
        }

        if (city != null){
            locationRecord.setCity(city.getName());
        }

        Country country;
        try {
            country = reader.city(InetAddress.getByName(ip)).getCountry();
        } catch (Exception e) {
            country = null;
        }

        if (country != null){
            locationRecord.setCountry(country.getName());
        }

        String subdivision;
        try {
            subdivision = reader.city(InetAddress.getByName(ip)).getSubdivisions().get(0).getName();
        } catch (Exception e) {
            subdivision = null;
        }

        if (subdivision != null){
            locationRecord.setSubdivision(subdivision);
        }

        Location location;
        try {
            location = reader.city(InetAddress.getByName(ip)).getLocation();
        } catch (Exception e) {
            location = null;
        }

        if (location != null){
            LatLonRecord latLonRecord = new LatLonRecord();
            latLonRecord.setLat(location.getLatitude());
            latLonRecord.setLon(location.getLongitude());

            locationRecord.setLatLonRecord(latLonRecord);
        }

        ObjectMapper mapper = new ObjectMapper();

        String json = null;
        try {
            json = mapper.writeValueAsString(locationRecord);
        } catch (JsonProcessingException e) {
            log.error(e.getStackTrace().toString());
        }

        return json;

    }

}
