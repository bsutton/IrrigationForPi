# 2.1.0
- upgrade all packages to the latest versions.
- upgraded to dart 3.x
- moved to using the archive library rather than relying on zip to be installed.
- added check that pig_build is run with priviledges.
- improvements to the build
- moved to the dcli chown function.

# 2.0.0
- upgraded to dart 3.x
- refreshed dependencies.
- upgraded maven-war plugin to a version hat supports java 11.
- removed the email validation as its causing a seg fault on the pi.
- removed the docker build option for the moment as docker fails on zfs doing a cross platform build.
- moved to using posix chown.
- fixed path problems on windows
- upgraded pacakges and fixed a few lints.

# 1.1.0
- upgraded pacakges and fixed a few lints.
# 1.0.30
- wip arm build.
- clean up of pig_arm_builder.
- add multithread options to mvn.
- fixed overlapping abbr for arg docker and debug.
- applied lint_hard
- wip docker build for arm.
- updated dcli version
- wip - build tools using qemu.
- improved docker daemon wait message.
- added test before creating log path. Added logic to start the docker service after installing it.

# 1.0.29
Tweaked privilges when installing.
released 1.0.28

# 1.0.28
Further tweaks to permissons.
# 1.0.27
Fixed permissions on the versiondir

# 1.0.27
Fixed permission for the version directory.

# 1.0.26
Docker commands need sudo.

# 1.0.25
timezone needs priviliges to write.

# 1.0.24
now fixing permissions from the pigation folder down.

# 1.0.24
Added priviliges when creating the versions folder to ensure we can write to it.

# 1.0.23
Fixed problems with sudo priviliges causing root to own the git repo.
Improved search logic for pig_stop.

# 1.0.22
Resolved issues with nginx-le docker upgrades.
# 1.0.21
Corrected mvn build directory.

# 1.0.20
Print out build version on start.

# 1.0.19
Added email as required for certbot.
Added logic to find the docker-compose file.
Added default location for docker-compose.yaml
Added back in requrirement to run as priviliged as setting the timezone requires it.
Fixed build/install issues when using compiled scripts

# 1.0.18
Fixed zip paths to match location used by build tools.
Added additional messages.
Added message informing use that build_tools will not be installed.
Added flag to control installation of tools
Added checks if we are installed from pub-cache.

# 1.0.17
Begin of work to allow a full build on the raspberry pi.
Added example sudo launch line. cleaned up the apt install lines so we aren't repeating ourselves. Changed the call to stop to the correct pig_stop.
moved docs to gitbooks.

# 1.0.16
docker login isn't required as nginx-le is public.
renamed dart_tools to build_tools

# 1.0.15
fixed the notes on how to complete the install.
upgraded to latest dcli so we can use default values in ask.

# 1.0.14
Removed the logic to download the zip as I've not found anywhere to store it.

# 1.0.13
upgraded to pub_release 3.0.
removed unused code.
renamed the hooks so they run in the correct order.
pre release hook to build the java code.

# 1.0.12
Updated to latest version of pub_release to fix bugs with hooks not running.

# 1.0.11

# 1.0.11
testing release process

# 1.0.10
testing release process

# 1.0.9

# 1.0.8
Merge pull request #2 from bsutton/dependabot/maven/org.apache.logging.log4j-log4j-core-2.13.2
Added builder so we can create a dart exe for arm.
this file shouldn't have been added.
converted to using docker and dart cli tools.
removed unused coded. removed references to snap as we are no longer using it. additional doco.
ignored stuff.
Bump log4j-core from 2.9.1 to 2.13.2
Merge pull request #1 from bsutton/dependabot/maven/com.google.guava-guava-24.1.1-jre
Bump guava from 23.6-jre to 24.1.1-jre
Update snapcraft.yaml
Merge branch 'master' of https://github.com/bsutton/IrrigationForPi
experiment with ant proxy
Update README.md
Update README.md
Update README.md
Added auth and a first run wizard for configuration.
 move to github for tomcat
progress on certbot integration
work on snapcraft.yaml and certbot
work on snap
Added in user management
ignore temp snap directories.
Work on getting the timer display to show when draining.
Fixed a bug where timers were not shutting down.
Made entity manager filter async.
EntityManager Injection and Onscreen timers
Timer counter for garden beds now functioning.
cert bot experiment.
missing files.
missing widget set.
Added support for lighting.

# 1.0.8
Merge pull request #2 from bsutton/dependabot/maven/org.apache.logging.log4j-log4j-core-2.13.2
Added builder so we can create a dart exe for arm.
this file shouldn't have been added.
converted to using docker and dart cli tools.
removed unused coded. removed references to snap as we are no longer using it. additional doco.
ignored stuff.
Bump log4j-core from 2.9.1 to 2.13.2
Merge pull request #1 from bsutton/dependabot/maven/com.google.guava-guava-24.1.1-jre
Bump guava from 23.6-jre to 24.1.1-jre
Update snapcraft.yaml
Merge branch 'master' of https://github.com/bsutton/IrrigationForPi
experiment with ant proxy
Update README.md
Update README.md
Update README.md
Added auth and a first run wizard for configuration.
 move to github for tomcat
progress on certbot integration
work on snapcraft.yaml and certbot
work on snap
Added in user management
ignore temp snap directories.
Work on getting the timer display to show when draining.
Fixed a bug where timers were not shutting down.
Made entity manager filter async.
EntityManager Injection and Onscreen timers
Timer counter for garden beds now functioning.
cert bot experiment.
missing files.
missing widget set.
Added support for lighting.

# 1.0.8
Fix in pub_release should now result in a git tag begin created
# 1.0.7
# 1.0.6
testing of the release process.
# 1.0.5
Test of new release/install processes.
# 1.0.4
Finally solved all of the version conflicts.
# 1.0.2
reduced dart version min to 2.7 so it installs onto a raspberry pi.
# 1.0.1
renamed binaries.
# 1.0.0
First release of dart tools for pigation.

