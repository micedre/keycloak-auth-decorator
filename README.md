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

  name  | description
----|----
User attribute | User attribute to fill with the value retrieved
Fetch Url      | Url to fetch, `#username`,`#email` and `#secret` are replaced with the corresponding values
Json Path      | Json expression to get the wanted value (should be in the form of [JsonPointer expression](https://tools.ietf.org/html/rfc6901))
Http headers   | Headers to send with the request (in the form `<Header Name>:<Value>`). The `#secret` expression in value is replaced with the config value.
Secret         | A secret to use in url or header (for a token, or other sensitive information). This field is write only.


## How it works 

This authenticator adds an user attribute by requesting an http api. The user attribute is refreshed everytime the authenticator is used.

## Example

Let's say you want to retrieve information on your keycloak user from github (maybe to check their organizations or get their github avatar). 

The github api publish an endpoint to search user by email here : https://api.github.com/search/users?q=email+in:email

In keycloak admin console, let's start by creating a new authtication flow with 2 executions : 
- User Password form
- Http User decorator

The 2 executions are marked as *Required*, that way, we assure to execute both actions.

The *Http User decorator* is configured as follow : 

 key | value
   --|--
User Attribute | `github_login`
Fetch Url      | `https://api.github.com/search/users?q=#email+in:email`
Json Path      | `/items/0/login` (we get the first result)

And we leave the others fields empty.

Then when authenticating an user, their email adress is searched on github and if found, a new attribute `github_login` is created for this user.