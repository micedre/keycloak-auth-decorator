package net.micedre.keycloak;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.Authenticator;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.AuthenticatorConfigModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;


public class HttpDecoratorAuthenticator implements Authenticator{

    static final Logger logger = Logger.getLogger(HttpDecoratorAuthenticator.class);

    @Override
    public void close() {
      // no-op
    }
  
    @Override
    public void authenticate(AuthenticationFlowContext context) {
      String url = getConfigSettingOrDefault(context, "fetch-url", null);
      if(url == null) {
        logger.debug("No url configured for http decorator");
        context.success();
        return;
      }
      UserModel user = context.getUser();
      String username = user.getUsername();
      String email = user.getEmail();
      url = url.replaceAll("#username", username);
      url = url.replaceAll("#email", email);

      String secret = getConfigSettingOrDefault(context, "secret", null);

      url = url.replaceAll("#secret", secret);
      logger.trace("Fetching "+ url);
      SimpleHttp httpRequest = SimpleHttp.doGet(url, context.getSession());
      String headers = getConfigSettingOrDefault(context, "headers", "");
      for(String header : headers.split("##")){
          if(header.contains(":")){
            String headerKey = header.split(":")[0];
            String headerValue = header.split(":")[1];
            httpRequest.header(headerKey, headerValue.replaceAll("#secret", secret));
          }
      }
      String jsonPath = getConfigSettingOrDefault(context, "json-path", "");
      String userAttribute = getConfigSettingOrDefault(context, "user-attribute", "decorator");
      try{
        JsonNode jsonResponse = httpRequest.acceptJson().asJson();
        JsonNode json = jsonResponse.at(jsonPath);
        List<String> values = new ArrayList<>();
        if(json.isArray()){
          for (final JsonNode objNode : json) {
            if(objNode.isValueNode()){
              values.add(objNode.asText());
            }
          }
        }else if(json.isValueNode()){
          values.add(json.asText());        
        }else{
          logger.info("Json pointer expr didn't return any values for json :"+jsonResponse);
        }
     
        context.getUser().setAttribute(userAttribute, values);
      }catch(Exception e){
        logger.info("Error when fetching url :" + e.getMessage());
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
