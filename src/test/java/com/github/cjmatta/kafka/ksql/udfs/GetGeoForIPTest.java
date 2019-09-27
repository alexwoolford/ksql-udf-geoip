package com.github.cjmatta.kafka.ksql.udfs;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;
import java.util.*;

public class GetGeoForIPTest {

  private GetGeoForIP udf;
  @Before
  public void setUp() {
    udf = new GetGeoForIP();
    File mmdb = new File("src/test/resources/GeoIP2-City.mmdb");
    configure(mmdb.getAbsolutePath());
  }

  @Test
  public void getLatLongIPTest() throws JsonProcessingException {
    Map<String, String> cityMap = new HashMap<>();
    cityMap.put("128.106.20.194", "{\"city\":\"Singapore\",\"country\":\"Singapore\",\"subdivision\":null,\"location\":{\"lat\":1.2929,\"lon\":103.8547}}");
    cityMap.put("68.80.162.250", "{\"city\":\"Philadelphia\",\"country\":\"United States\",\"subdivision\":\"Pennsylvania\",\"location\":{\"lat\":39.952,\"lon\":-75.1814}}");
    cityMap.put("49.217.88.22", "{\"city\":\"Dingfu\",\"country\":\"Taiwan\",\"subdivision\":\"New Taipei\",\"location\":{\"lat\":25.0798,\"lon\":121.8523}}");
    cityMap.put("138.197.66.165", "{\"city\":\"Clifton\",\"country\":\"United States\",\"subdivision\":\"New Jersey\",\"location\":{\"lat\":40.8364,\"lon\":-74.1403}}");

    Iterator it = cityMap.entrySet().iterator();
    while(it.hasNext()) {
      Map.Entry pair = (Map.Entry) it.next();
      assertEquals(pair.getValue(), udf.getgeoforip(pair.getKey().toString()));
    }
  }

  private void configure(String mmdbPath) {
    Map<String, String> config = new HashMap<String, String>();
    config.put("ksql.functions.getgeoforip.geocity.db.path", mmdbPath);
    udf.configure(Collections.unmodifiableMap(new LinkedHashMap<String, String>(config)));
  }
}