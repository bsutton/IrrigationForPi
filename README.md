Pi-gation
============
An irrigation and out door lighting controller for the Raspbery PI.

To use Pi-gation you need a Raspberry PI, one or more relay boards wired up to digital IO pins and some Solinoid valves or lights that you want to control.


Overview
========

IrrigationForPi is a web app designed to allow you to control external lighting and irrigation systems from a mobile device.

The app allows you to configure Pins on the Pi to control various devices such as lights and valves for an irrigation system.

In theory the app can be used to control any device attached to a Pi but it has specific interfaces that are fashioned around 
configuring irrigation and lighting systems.

It only takes a few minutes to get this web app up and running so give it a try :)

Contributes to this project are strongly encouraged so post your patches.

Build
=======
download the source:

install maven
`sudo apt-get install maven`

cd to the directory you downloaded the source code to and run:

`mvn install`

This generaates a .war file in the `targets` directory ready to install.

Installation
==========
We use and recommend that you use [noobs](https://www.raspberrypi.org/downloads/noobs/) to install an OS on your raspberry pi.
The following install guide assumes that you are running [raspbian](http://raspbian.org/) (an install option from noobs).

We recommend that you use tomcat to run the irrigation control on your raspberry pi using raspbarian.

# Install tomcat 8
To install using tomcat 8 

If you are using a different version of tomcat then substitute '8' for your version no. in each of the below commands.

`sudo apt-get install tomcat8`

# To provide tomcat access to gpio pins

`sudo adduser tomcat8 gpio`

Download the latest irrigation-XX.XX.XX.war file.
Rename the war to:
`mv irrigation-XX.XX.XX.war irrigation.war`

Create a directory for the derby db at:

mkdir -p /home/pi/irrigationDb

Set the permissions to:
sudo chown tomcat8:tomcat8 /home/pi/irrigationDb

Copy the war file into the tomcat 8 webapp directory.

`cp irrigation.war /var/lib/tomcat8/webapps`

Start tomcat

`sudo service tomcat8 start`

Tail the tomcat log to check that everything starts ok.

`tail -f /var/log/tomcat8/catalina.out`

You now should have a operational web application.
You can access the web app via:

`http://<pi host name or ip>:8080/irrigation`

Now that your system is up and running you need to click the 'Configuration' button and
define each of your End Point mappings.

An end point is a light or valve that you have wired up to a Raspberry Pi GPIO pin.

You can also test each End Point as you define it.

 
Technology
==========

IrrigationForPi uses the following technology (if you care about such things). You don't need to know this to use or install the app.

* Java 8
* Tomcat 8
* EclipseLink (JPA)
* Derby (database).
* Vaadin 8 framework.
