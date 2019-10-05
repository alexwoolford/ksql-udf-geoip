package com.github.cjmatta.kafka.ksql.udfs;

import org.apache.kafka.connect.data.Struct;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasToString;

public class GeoipLookupTest {

  private GeoipLookup udf;
  @Before
  public void setUp() {
    udf = new GeoipLookup();
    File mmdb = new File("src/test/resources/GeoIP2-City.mmdb");
    configure(mmdb.getAbsolutePath());
  }

  @Test
  public void geoipLookupHappyPathTest() {

    Map<String, Struct> cityMap = new HashMap<>();

    GeoSchema geoSchema = new GeoSchema();

    Struct latlon = new Struct(geoSchema.getLatLonSchema())
              .put("longitude", 121.8523)
              .put("latitude", 25.0798);

    Struct geolocation = new Struct(geoSchema.getGeoipLocationSchema())
              .put("city", "Dingfu")
              .put("country", "Taiwan")
              .put("subdivision", "New Taipei")
              .put("location", latlon);

    assertThat(udf.getgeoforip("49.217.88.22"), hasToString(geolocation.toString()));

  }

  @Test
  public void geoipLookupNullIPTest() {
    assertThat(udf.getgeoforip(null), hasToString("Struct{}"));
  }

  @Test
  public void geoipLookupNotIPAddressTest() {
    assertThat(udf.getgeoforip("not an IP address"), hasToString("Struct{}"));
  }

  private void configure(String mmdbPath) {
    Map<String, String> config = new HashMap<String, String>();
    config.put("ksql.functions.getgeoforip.geocity.db.path", mmdbPath);
    udf.configure(Collections.unmodifiableMap(new LinkedHashMap<String, String>(config)));
  }

}