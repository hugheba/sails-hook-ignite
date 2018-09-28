# sails-hook-ignite

A sails hook to integrate your Sails app into an Apache Ignite grid.

> This hook only works for Sails apps running under GraalVM/GraalJS implementation of node.

## Version Info



## Quick Start

### Installation

From your Sails app:

`$ npm install sails-hook-ignite --save`

### Configuration

Create a new config file in your project `config/ignite.js`.

Copy and modify the sample configuration file found [here](sails-hook/config/ignite.js)

## Project


### Build IgniteBridge.jar



    $> ./gradlew module-java-bridge:shadowJar

### Example App

The git repo contains an example Sails app running `sails-hook-ignite`.

To boot the example app, run the following command:

    $> ./gradlew module-sails-example-app:runExampleApp
    
Once booted you can browse to [http://localhost:1337](http://localhost:1337) in your web browser.