package com.github.cjmatta.kafka.ksql.udfs;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;

public class GeoSchema {

    public Schema getLatLonSchema(){

        Schema latLonSchema = SchemaBuilder.struct()
                .field("LON", Schema.OPTIONAL_FLOAT64_SCHEMA)
                .field("LAT", Schema.OPTIONAL_FLOAT64_SCHEMA)
                .optional()
                .build();

        return latLonSchema;
    }

    public Schema getGeoipLocationSchema(){

        Schema geoipLocationSchema = SchemaBuilder.struct()
                .field("CITY", Schema.OPTIONAL_STRING_SCHEMA)
                .field("COUNTRY", Schema.OPTIONAL_STRING_SCHEMA)
                .field("SUBDIVISION", Schema.OPTIONAL_STRING_SCHEMA)
                .field("LOCATION", getLatLonSchema())
                .optional()
                .build();

        return geoipLocationSchema;
    }

}
