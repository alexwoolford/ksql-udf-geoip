KSQL example UDF for looking up a geolocation for an IP address.
### Pre-requisite
You'll need to supply your own GeoCity database, specify it's location using the `function.getgeoforip.geocity.db.path` property in your ksql-server.properties before starting KSQL.

For an RPM-based install of KSQL, I set the following properties in `/etc/ksql/ksql-server.properties`:

    ksql.extension.dir=/opt/ksql-extension
    ksql.functions.getgeoforip.geocity.db.path=/opt/mmdb/GeoIP2-City.mmdb

### Build
```
mvn clean package
```

and move the jar `target/ksql-udf-geoip-1.0-SNAPSHOT-jar-with-dependencies.jar` into the `ksql.extension.dir` directory in your KSQL installation. 

### Usage
```sql
ksql> select ip, getgeoforip(ip) as geo from WEBLOG;

17.248.185.37 | {CITY=Dallas, COUNTRY=United States, SUBDIVISION=Texas, LOCATION={LON=-96.8217, LAT=32.7787}}
73.203.107.160 | {CITY=Lafayette, COUNTRY=United States, SUBDIVISION=Colorado, LOCATION={LON=-105.0974, LAT=39.997}}
104.244.43.131 | {CITY=Lost Creek, COUNTRY=United States, SUBDIVISION=West Virginia, LOCATION={LON=-80.3777, LAT=39.1725}}
204.11.201.10 | {CITY=Seattle, COUNTRY=United States, SUBDIVISION=Washington, LOCATION={LON=-122.2972, LAT=47.63}}
35.174.159.55 | {CITY=Ashburn, COUNTRY=United States, SUBDIVISION=Virginia, LOCATION={LON=-77.4728, LAT=39.0481}}
52.37.243.173 | {CITY=Boardman, COUNTRY=United States, SUBDIVISION=Oregon, LOCATION={LON=-119.7143, LAT=45.8491}}
69.41.163.31 | {CITY=Dallas, COUNTRY=United States, SUBDIVISION=Texas, LOCATION={LON=-96.8336, LAT=32.7908}}
108.177.111.189 | {CITY=null, COUNTRY=United States, SUBDIVISION=null, LOCATION={LON=-97.822, LAT=37.751}}
74.125.69.188 | {CITY=Mountain View, COUNTRY=United States, SUBDIVISION=California, LOCATION={LON=-122.0839, LAT=37.3861}}
104.24.17.51 | {CITY=null, COUNTRY=United States, SUBDIVISION=null, LOCATION={LON=-97.822, LAT=37.751}}
184.105.182.16 | {CITY=null, COUNTRY=United States, SUBDIVISION=null, LOCATION={LON=-97.822, LAT=37.751}}
52.38.181.16 | {CITY=Boardman, COUNTRY=United States, SUBDIVISION=Oregon, LOCATION={LON=-119.7143, LAT=45.8491}}
3.212.78.185 | {CITY=Ashburn, COUNTRY=United States, SUBDIVISION=Virginia, LOCATION={LON=-77.4728, LAT=39.0481}}
104.18.3.30 | {CITY=null, COUNTRY=United States, SUBDIVISION=null, LOCATION={LON=-97.822, LAT=37.751}}
3.16.199.167 | {CITY=Columbus, COUNTRY=United States, SUBDIVISION=Ohio, LOCATION={LON=-83.0235, LAT=39.9653}}
173.243.139.53 | {CITY=null, COUNTRY=United States, SUBDIVISION=null, LOCATION={LON=-97.822, LAT=37.751}}

```

See the UDF in action:

[![Geo-enrich syslog with KSQL and Elastic](https://img.youtube.com/vi/sHfp2AIeZGw/0.jpg)](https://www.youtube.com/watch?v=sHfp2AIeZGw)

