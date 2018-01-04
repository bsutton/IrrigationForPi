 
 export PI4J_PLATFORM=Simulated
 export SimulatedPlatform="RaspberryPi GPIO Provider"

 # mvn jetty:run

 #mvn exec:java -Dexec.mainClass="au.org.noojee.irrigation.JettyServer"

java -cp target/irrigation-1.0-SNAPSHOT-classes.jar au.org.noojee.irrigation.JettyServer
