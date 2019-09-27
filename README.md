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
ksql> select ip, getgeoforip(ip) as geo from SYSLOG;

64.26.151.36 | {"city":"Ottawa","country":"Canada","subdivision":"Ontario","location":{"lat":45.3548,"lon":-75.5773}}
66.117.56.37 | {"city":"Ashburn","country":"United States","subdivision":"Virginia","location":{"lat":39.0127,"lon":-77.5342}}
173.243.138.195 | {"city":null,"country":"United States","subdivision":null,"location":{"lat":37.751,"lon":-97.822}}
65.210.95.239 | {"city":"Rockville","country":"United States","subdivision":"Maryland","location":{"lat":39.0828,"lon":-77.1674}}
209.222.147.36 | {"city":"Ottawa","country":"United States","subdivision":"Illinois","location":{"lat":41.3526,"lon":-88.8416}}
96.45.33.64 | {"city":"Sunnyvale","country":"United States","subdivision":"California","location":{"lat":37.3773,"lon":-122.0194}}
66.117.56.42 | {"city":"Ashburn","country":"United States","subdivision":"Virginia","location":{"lat":39.0127,"lon":-77.5342}}
```
