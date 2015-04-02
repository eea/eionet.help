Eionet help module 3.0
======================

This code is migrated from https://svn.eionet.europa.eu/repositories/Reportnet/helpservice and the package name is changed from com.tee.uit.help to eionet.help.

Usage
-----
The new version 3.0 needs this added to dependencies in Maven:

```xml
<dependency>
    <groupId>eionet</groupId>
    <artifactId>help</artifactId>
    <version>3.0</version>
</dependency>
```
The old version uses this.
```xml
<dependency>
    <groupId>eionet</groupId>
    <artifactId>uit-help</artifactId>
    <version>1.1</version>
</dependency>
```

Configuration
-------------
The module need a database connection to read and write to the tables HLP_AREA and HLP_SCREEN. In versions 1 and 2 the module loaded the uit.properties file and read the following properties: `db.driver`, `db.url`, `db.user` and `db.pwd`. The parameters didn't have to be the same as the main application, but it was common practice. From version 3 you can use JNDI. The module will scan JNDI for Tomcat resource under `help/`. If a `help/propertiesfile` value is found the JNDI names are supplemented with values from the file in the value. If no JNDI variables are found the module falls back to a `help.properties` file in the class path.
After having loaded all the properties, the module will first look for the name `help/jndiname`, and the value is expected to be a string. If found the value is the JNDI name to look up the data source under. This data source is required to exist in the `jdbc` sub-context. It makes it possible to reuse the connection pool from the main application. If not found then the data source is looked up under `jdbc/helpdb`. If it is not available the module will create a connection from `db.driver`, `db.url`, `db.user` and `db.pwd`.


