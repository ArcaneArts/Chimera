# Chimera
A server/client platform for designing interconnected flutter apps &amp; servers

## Setup
1. Clone into Chimera
2. Open Chimera in Intellij
3. Sync Gradle Project

## Create a Service
To create a new service, or a fragment of the Chimera Server
1. Execute chimera-config.bat (root directory)
2. Once the configurator has started, execute `newService <UpperCammelCaseName>`
3. Once the configurator finishes, close the console & sync gradle projects

## Deleting a Service
1. Delete the project module files through Intellij
2. Edit the settings.gradle file & remove your module include
3. Open the module configuration in Intellij & remove your module
