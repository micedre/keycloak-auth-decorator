package net.micedre;

import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

public class HttpDecoratorAuthenticator implements Authenticator{
    @Override
    public void close() {
      // no-op
    }
  
    @Override
    public void authenticate(AuthenticationFlowContext context) {
      String url = getConfigSettingOrDefault(context, "fetch-url", null);
      if(url == null) {
        context.success();
        return;
      }

      SimpleHttp httpRequest = SimpleHttp.doGet(url, context.getSession());
      String headers = getConfigSettingOrDefault(context, "headers", "");
      for(String header : headers.split("|")){
          if(header.contains(":")){
            String headerKey = header.split(":")[0];
            String headerValue = header.split(":")[1];
            httpRequest.header(headerKey, headerValue);
          }
      }
      String jsonPath = getConfigSettingOrDefault(context, "json-path", "");
      String userAttribute = getConfigSettingOrDefault(context, "user-attribute", "decorator");
      try{
        JsonNode json = httpRequest.acceptJson().asJson();
        String value = json.path(jsonPath).asText();
        context.getUser().setAttribute(userAttribute, Arrays.asList(value));
      }catch(Exception e){
        // do nothing, we don't care
      }
      context.success();
    }
  
    private String getConfigSettingOrDefault(
        AuthenticationFlowContext context, String key, String defaultValue) {
  
      AuthenticatorConfigModel authenticatorConfig = context.getAuthenticatorConfig();
      if (authenticatorConfig == null) {
        return defaultValue;
      }
      Map<String, String> config = authenticatorConfig.getConfig();
      if (config == null) {
        return defaultValue;
      }
      return config.getOrDefault(key, defaultValue);
    }
  
    @Override
    public void action(AuthenticationFlowContext context) {
      // no-op
    }
  
    @Override
    public boolean requiresUser() {
      return true;
    }
  
    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
      return true;
    }
  
    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
      // no-op
    }
}
