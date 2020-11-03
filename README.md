Pi-gation
============
An irrigation and out door lighting controller for the Raspbery PI.

To use Pi-gation you need a Raspberry PI, one or more relay boards wired up to digital IO pins and some solenoid valves or lights that you want to control.


WARNING WARNING
===============

Water can be surprisingly destructive. Even a small leak can do a lot of damage.

Pi-gation is a work in progress, so there are REAL risks that it may do something wrong that could end up with a serious water flow for extended periods!!!

Having said that I've been using it for three years without issue.

If you should choose to use Pi-gation you MUST only do so at your OWN RISK.

I make no warranties that Pi-gation is fit for purpose and I guarantee you that it has LOTS OF BUGS.

So use it carefully and monitor its operation carefully until you are comfortable it is operating correctly in your environment.

During early operation you should be turning the master tap OFF when you are not physically monitoring your watering system.



Overview
========

Pi-gation is a web app designed to allow you to control external lighting and irrigation systems from a mobile device.

The app allows you to configure Pins on the Pi to control various devices such as Lights and Valves for an irrigation system.

In theory the app can be used to control any device attached to a Pi but it has specific interfaces that are fashioned around 
configuring irrigation and lighting systems.

It only takes a few minutes to get this web app up and running so give it a try :)

Contributes to this project are strongly encouraged so post your patches.

You can read the Pi-gation [user manual here](https://github.com/bsutton/IrrigationForPi/wiki)

Build
=======
On your local desktop

This process could be done on the PI but it would take a very long time.


Configure the build environment

```
sudo apt install openjdk-11-jdk-headless maven git dart

# log out and back in before continuing.

pub global activate dcli
pub global activate pigation

git clone git@github.com:bsutton/IrrigationForPi.git

cd IrrigationForPi/dart_tools
pig_build
```

The pig_build command outputs a zip file which you need to copy to the raspberry pi.

```
scp install_pigation.zip <ip address>:
```

Once the file has copied go to the section on installing.


Installation
==========

On the raspbery pi 

The install process needs at least 2 GB of ram or swap so make certain you have
allocated sufficient swap before you start the install.

If you don't have sufficient swap then the `pub global activate` command are likely to hang.


```
sudo apt install dart unzip

## logout of the pi and log back in before continuing.

pub global activate dcli
pub global activate pigation

sudo apt install docker 

pig_install

```

Note: I've had trouble with the raspberry pi running out of memory during this phase so shutdown any process that aren't needed.
On my 1GB pi I had to [extend my swap file](https://bogdancornianu.com/change-swap-size-in-ubuntu/) to 4GB to get the step to complete:



Pi-gation is now running and waiting for you to configure your garden beds and lighting.

Open a web browser on you PC or Phone and access the ip address of fqdn of you raspberry pi.

 
Technology
==========

IrrigationForPi uses the following technology (if you care about such things). You don't need to know this to use or install the app.

* Java 8
* Tomcat 8
* EclipseLink (JPA)
* Derby (database).
* Vaadin 8 framework.
* Docker
* Dart
* DCli

