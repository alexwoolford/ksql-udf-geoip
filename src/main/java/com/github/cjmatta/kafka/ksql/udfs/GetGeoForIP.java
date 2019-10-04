package com.github.cjmatta.kafka.ksql.udfs;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.DatabaseReader.Builder;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Subdivision;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import org.apache.kafka.common.Configurable;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UdfDescription(
        name = "getgeoforip",
        description = "Function to lookup ip -> geo information "
)
public class GetGeoForIP implements Configurable {
    private DatabaseReader reader;
    private Logger log = LoggerFactory.getLogger(GetGeoForIP.class);

    public GetGeoForIP() {
    }

    public void configure(Map<String, ?> props) {
        String KSQL_FUNCTIONS_GETGEOFORIP_GEOCITY_DB_PATH_CONFIG = "ksql.functions.getgeoforip.geocity.db.path";
        if (!props.containsKey(KSQL_FUNCTIONS_GETGEOFORIP_GEOCITY_DB_PATH_CONFIG)) {
            throw new ConfigException("Required property " + KSQL_FUNCTIONS_GETGEOFORIP_GEOCITY_DB_PATH_CONFIG + " not found!");
        } else {
            String geoCityDbPath = (String)props.get(KSQL_FUNCTIONS_GETGEOFORIP_GEOCITY_DB_PATH_CONFIG);

            try {
                File database = new File(geoCityDbPath);
                this.reader = (new Builder(database)).build();
                this.log.info("loaded GeoIP database from " + geoCityDbPath.toString());
            } catch (IOException var6) {
                this.log.error("Problem loading GeoIP database: " + var6);
                throw new ExceptionInInitializerError(var6);
            }
        }
    }

    @Udf(description = "returns lat/lon from IP input")
    public Struct getgeoforip(@UdfParameter(value = "ip", description = "the IP address to lookup in the geoip database", schema="STRUCT<city VARCHAR, country VARCHAR, subdivision VARCHAR, STRUCT<location STRUCT<longitude DOUBLE, latitude DOUBLE>>>") String ip) {

        City city;
        try {
            city = this.reader.city(InetAddress.getByName(ip)).getCity();
        } catch (Exception var13) {
            city = null;
        }

        Country country;
        try {
            country = this.reader.city(InetAddress.getByName(ip)).getCountry();
        } catch (Exception var12) {
            country = null;
        }

        String subdivision;
        try {
            subdivision = ((Subdivision)this.reader.city(InetAddress.getByName(ip)).getSubdivisions().get(0)).getName();
        } catch (Exception var11) {
            subdivision = null;
        }

        Location location;
        try {
            location = this.reader.city(InetAddress.getByName(ip)).getLocation();
        } catch (Exception var10) {
            location = null;
        }

        Schema latLonSchema = SchemaBuilder.struct().name("location").field("longitude", Schema.OPTIONAL_FLOAT64_SCHEMA).field("latitude", Schema.OPTIONAL_FLOAT64_SCHEMA).build();
        Struct latlon = null;
        if (location != null) {
            latlon = (new Struct(latLonSchema)).put("longitude", location.getLongitude()).put("latitude", location.getLatitude());
        }

        Schema geoipLocationSchema = SchemaBuilder.struct().name("geolocation").field("city", Schema.OPTIONAL_STRING_SCHEMA).field("country", Schema.OPTIONAL_STRING_SCHEMA).field("subdivision", Schema.OPTIONAL_STRING_SCHEMA).field("location", latlon.schema()).build();
        Struct result = (new Struct(geoipLocationSchema)).put("city", city.getName()).put("country", country.getName()).put("subdivision", subdivision).put("location", latlon);
        return result;
    }

}
