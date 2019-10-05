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

17.134.127.249 | {city=null, country=United States, subdivision=null, location={longitude=-97.822, latitude=37.751}}
35.161.94.40 | {city=Boardman, country=United States, subdivision=Oregon, location={longitude=-119.7143, latitude=45.8491}}
209.115.181.108 | {city=Edmonton, country=Canada, subdivision=Alberta, location={longitude=-113.4178, latitude=53.4154}}
35.161.94.40 | {city=Boardman, country=United States, subdivision=Oregon, location={longitude=-119.7143, latitude=45.8491}}
35.161.94.40 | {city=Boardman, country=United States, subdivision=Oregon, location={longitude=-119.7143, latitude=45.8491}}
52.25.172.95 | {city=Boardman, country=United States, subdivision=Oregon, location={longitude=-119.7143, latitude=45.8491}}
13.33.252.119 | {city=Seattle, country=United States, subdivision=Washington, location={longitude=-122.3451, latitude=47.6348}}
17.134.127.250 | {city=null, country=United States, subdivision=null, location={longitude=-97.822, latitude=37.751}}
74.125.124.189 | {city=Mountain View, country=United States, subdivision=California, location={longitude=-122.0748, latitude=37.4043}}
52.37.243.173 | {city=Boardman, country=United States, subdivision=Oregon, location={longitude=-119.7143, latitude=45.8491}}
193.29.63.150 | {city=null, country=Germany, subdivision=null, location={longitude=9.491, latitude=51.2993}}

```
