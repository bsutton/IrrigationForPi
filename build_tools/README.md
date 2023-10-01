Pi-gation
============
An irrigation and out door lighting controller for the Raspbery PI.

To use Pi-gation you need a Raspberry PI, one or more relay boards wired up to digital IO pins and some solenoid valves or lights that you want to control.

# DNS requirements
Pigration use Let's Encrypt to obtain a SSL certificate.

To support this process pigation requires tha that you have a registered domain name with an A record pointing to your home router.

There are ways around this requirement but I don't currently have the time to document them.

Contributions around this issue are welcomed.

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

# preparation
Whilst you can build pigation on your raspberry PI we recommend that you run the build
on your local PC as running the build on the PI is likey to take over an hour.

If you are going to build directly on your raspberry PI then you don't need to install Dart on your desktop system.

* install dart on your desktop system 
* install dart on your raspberry PI
* obtain a static IP address for your home router
* obtain a domain name
* add an A record to your DNS server pointing at your home static IP
* configure your DHCP server to reserve a fixed IP address for the PI
* Setup a NAT rules to port forward port 80 and port 443 (optional) to your PI.

# Build
To build pigation you need to have Dart 3.x installed

Note: if you are using Windows to do a trial build you will need to use wsl2 (e.g. launch a Ubuntu terminal in Windows.) Once you have opened an wsl2 terminal you will need to follow the linux install instructions.

Refer to the Dart install guide for details 

https://dart.dev/get-dart

Once is installed you can now activate pigation.

Note: the activation process can take up to 10 minutes.

```bash
dart pub global activate pigation
```

To build pigation:

Note: this is a lengthy process and you can expect it to take up to an hour on your PI.

```bash
sudo env "PATH=$PATH" pig_build
```

The core pigation web service is written in Java so the builder needs to install java and a number of other packages to build pigation.

# install
In order to run the install you need to have a DNS and NAT setup.

# Static IP
Run pigation you need a static ip with a DNS A record pointing to your IP address.

# DNS
Once pig_build completes you are now ready to install pigation.



Run:
```bash
pig_install
```

## Certificates and remote access
pigation runs as a web server that you can access from any browser.
Modern browsers require that your web server provides an SSL certificate.

pigation uses the free Let's Encrypt service to acquire an SSL certificate.

These certificates need to be renewed every three months. Pigation automates the renewal process however you will need a static IP for the renewal process to work. 
If you don't have a static IP then you can still obtain an SSL certificate but you will need to run pig-reconfigure every three months to manually re-acqure a certificate.


# port 80 for certificate acquisition
The pigation install process automates the acquisition of an SSL certificate, however to do so you need to open up port 80 on your home router and set up a NAT rule to forward requests to your PI.

# port 443 to remotely water your garden
By opening up port 443 you can remotely water your garden.
For this to work you will need a static IP or use a dynamic DNS service.
Ideally you would also acquire a domain name so that you can access d

You will need to create a NAT rule on your router that forwards requests on port 443 to your raspberry PI.


** SECURITY RISK **
I should note that currently pigation is completely open.  If some knows the IP address of you home network then they can remotely water you garden.

This does have the risk that someone could intentionally flood you property by remoting turning your irrigation system on.

We really need to add at a least a simple password to the front end :)

## Enable Port forward
If you want to remotely control you raspberry pi you will need to enable 
If you are running your raspberry pi on your home network you will need to set up a port forward on your router to the raspberry pi.





You will need to port forward both port 80 and port 443.



# start / stop

You can start and stop pigation via:

```bash
pig_start
pig_stop
```


Pi-gation is now running and waiting for you to configure your garden beds and lighting.

 
Technology
==========

IrrigationForPi uses the following technology (if you care about such things). You don't need to know this to use or install the app.

* Java 8
* Tomcat 8
* EclipseLink (JPA)
* Derby (database).
* Vaadin 8 framework.
* Dart 3.x

# Old install instructions
The following is the original install instructions.
We now have a build/install system written Dart which should allow even the most technically challenged person to get pigation up and running.

Build
=======
download the source:

install maven
`sudo apt-get install maven`

cd to the directory you downloaded the source code to and run:

`mvn install`

This generates a .war file in the `targets` directory ready to install.


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

Enable SSL
=================
If you want to install the web app as a 'desktop' app on your mobile device you will need to enable SSL (we strongly recommend this regardless).

To enable SSL.

Install Apache
===============

`sudo apt install apache2`

`sudo a2enmod ssl`

We use the proxy to talk to tomcat.
`sudo a2enmod proxy`
`sudo a2enmod proxy_http`
`sudo a2enmod rewrite`

Start the Apache2 service

`sudo service apache2 start`

Enter the PI's host name in 
`/etc/hostname`

Set the ServerName in:
`/etc/apache2/apache2.conf`

Just add the following at the end of the file replacing <hostname> with your host name.

`ServerName <hostname>`

Install Certbot and enable keys
===============================
The LetsEncrypt project provides free SSL certificates.
To generate a certificate you need to install certbot.

`sudo apt install certbot python-certbot-apache`

# Generate the keys:

Run Certbot to generate the keys and validate your sanyerver.
NOTE: the above apache web server MUST be visible publicly for the doman
name you use in the following certbot line as your server is used to validate 
that you are the owner.

# Enable Port forward
If you are running your raspberry pi on your home network you will need to set up a port forward on your router to the raspberry pi.
You will need to port forward both port 80 and port 443.

Copy the following into the file to /etc/apache2/sites-enabled/irrigation.conf

```
# This first Virtual host just forces all request to use HTTPS 
<VirtualHost *:80>any
        ServerAdmin <your email address>
        ServerName <fqdn to your pi>
        <IfModule mod_rewrite.c>
            # Redirect request to SSL port.
            RewriteEngine on
            RewriteCond %{HTTPS} off
            RewriteRule (.*) https://%{HTTP_HOST}%{REQUEST_URI}
        </IfModule>
</VirtualHost>
```

# Generate you certificates
Run certbot to generate your certificate an install it into apache.

First ensure that apache2 is running

`sudo service apache2 start`

Now generate and install the certificates.
`sudo certbot  --apache  -d <fqnd of pi> -m <email>`

Replace <fqdn> with the fully qualified domain name of your Pi.
Replace <email> with your email address.

Certbot will have generated a new file for the SSL module:

`/etc/apache2/sites-enabled/irrigation-le-ssl.conf`

Edit this file and adjust it so that 
```

<VirtualHost *:443>
        ServerAdmin <you email address>
        ServerName <fqdn of your pi>

    # As we are using SSL you need an SSL cert. We use certbot from LetsEncrypt
    SSLCertificateFile /etc/letsencrypt/csr/0000_csr-certbot.pem
    SSLCertificateKeyFile /etc/letsencrypt/keys/0000_key-certbot.pem

    # The path to your irrigation instance
    DocumentRoot /var/lib/tomcat8/webapps/irrigation
    <Directory />
        Options FollowSymLinks
        AllowOverride None
    </Directory>
    <Directory /var/lib/tomcat8/webapps/irrigation/>
        Options Indexes FollowSymLinks MultiViews
        AllowOverride None
        Order allow,deny
        allow from all
    </Directory>

    ErrorLog /var/log/apache2/error.log

    # Possible values include: debug, info, notice, warn, error, crit,
    # alert, emerg.
    LogLevel warn

    CustomLog /var/log/apache2/access.log combined

    ProxyRequests Off
    ProxyPreserveHost On
    ProxyPassReverse /irrigation http://127.0.0.1:8080/irrigation
    ProxyPass /irrigation http://127.0.0.1:8080/irrigation
    <Location />
        Order Allow,Deny
        Allow from AlL
    </Location>
    RewriteEngine On
    RewriteRule ^$   /irrigation/ [R]
    RewriteRule ^/$  /irrigation/ [R]
</VirtualHost>

```

# Restart Apache

`sudo service apache2 restart'

Now that your system is up and running you need to click the 'Configuration' button and
define each of your End Point mappings.

An end point is a light or valve that you have wired up to a Raspberry Pi GPIO pin.

You can also test each End Point as you define it.

# Install using the snap installer (recommended)
Download UbuntuCore for the Raspberry Pi from here:
https://developer.ubuntu.com/core/get-started/raspberry-pi-2-3

Follow the instruction to setup a Ubuntu SSO account and install Ubuntu Core as per those instructions.

Pre-requisites
Your raspberri PI must be visible to the public internet and you need to have a real host name for you pi. (i.e you need to obtain and register a domain and create an A record pointing at your PI).
Once installed connect to your PI via ssh and run the following commands:


* sudo snap install pi-gation

pi-gation is now installed. Lets now get a certificate:

* sudo pi-gation.getcert <youremail> <fqdn of pi>

Your now have a certificate.

Connected to pi gation using your browser and the fqdn of your PI.

Pi-gation will run the FirstRun Wizard and ask a few setup questions.
Once you have answered the questions you must restart tomcat or you PI.

* sudo snap restart pi-gation.tomcat

Pi-gation is now running and waiting for you to configure your garden beds and lighting.

 
