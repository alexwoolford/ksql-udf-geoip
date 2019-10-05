package com.github.cjmatta.kafka.ksql.udfs;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

public class GeoSchema {

    public Schema getLatLonSchema(){

        Schema latLonSchema = SchemaBuilder.struct()
                .name("location")
                .field("longitude", Schema.OPTIONAL_FLOAT64_SCHEMA)
                .field("latitude", Schema.OPTIONAL_FLOAT64_SCHEMA)
                .build();

        return latLonSchema;
    }

    public Schema getGeoipLocationSchema(){

        Schema geoipLocationSchema = SchemaBuilder.struct()
                .name("geolocation")
                .field("city", Schema.OPTIONAL_STRING_SCHEMA)
                .field("country", Schema.OPTIONAL_STRING_SCHEMA)
                .field("subdivision", Schema.OPTIONAL_STRING_SCHEMA)
                .field("location", getLatLonSchema());

        return geoipLocationSchema;
    }

}
