# Keycloak Auth Decorator

Simple keycloak authenticator to add an attribute from an http json API to an authenticated user.
The idea was to get user info from another source than the configured user storage. For example, it can be used to fetch info from github API...

## How to install

Build the jar :

```
mvn package
```

Copy it in `$keycloak/standalone/deployments`

## How to use 

Add the http user decorator to authenticator flow (:warning: you need to set it as required, so you will need to wrap the old authenticator config in a generic flow to set it both as required )

Don't forget to configure the new authenticator, for everything to work.

## Configuration



## How it works 

This authenticator adds an user attribute by requesting an http api. The user attribute is refreshed everytime the authenticator is used.
