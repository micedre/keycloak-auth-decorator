package net.micedre.keycloak;

import java.util.List;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;

public class HttpDecoratorAuthenticatorFactory implements AuthenticatorFactory {

    @Override
    public void close() {
        //NO-OP
    }

    @Override
    public Authenticator create(KeycloakSession session) {
        return new HttpDecoratorAuthenticator();
    }

    @Override
    public String getId() {
        return "http-user-decorator";
    }

    @Override
    public void init(Config.Scope arg0) {
        //NO-OP
    }

    @Override
    public void postInit(KeycloakSessionFactory arg0) {
        //NO-OP
    }

    @Override
    public String getDisplayType() {
        return "Http user decorator";
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }
    private static AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
        AuthenticationExecutionModel.Requirement.REQUIRED,
        AuthenticationExecutionModel.Requirement.ALTERNATIVE,
        AuthenticationExecutionModel.Requirement.DISABLED
      };
    
      @Override
      public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
      }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return true;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return ProviderConfigurationBuilder.create().property().name("user-attribute").label("User attribute")
                .helpText("Name of the attribute to add to the user").type(ProviderConfigProperty.STRING_TYPE).add()
                .property().name("fetch-url")
                .helpText(
                        "Url to fetch for the attribute (expects json),  $(mail), $(username) are replaced by the authenticated user mail and username")
                .label("Fetch Url").type(ProviderConfigProperty.STRING_TYPE).add().property().name("json-path")
                .label("Json Path").helpText("Json path expression to extract the attribute")
                .type(ProviderConfigProperty.STRING_TYPE).add().property().type(ProviderConfigProperty.STRING_TYPE)
                .name("headers").label("Http Headers in the form : `<key>:<value>|<key>:<value>`").add().build();
        
    }

    @Override
    public String getHelpText() {
        return "Get attribute from an http api";
    }

}
