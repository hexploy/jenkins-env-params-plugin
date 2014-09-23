package org.hexploy.jenkins.plugins.envparam;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.ParameterValue;
import hudson.util.VariableResolver;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.hexploy.jenkins.plugins.envparam.EnvParamDefinition.EnvParamDescriptor;
import static org.hexploy.jenkins.plugins.envparam.EnvParamDefinition.EnvParamDescriptor.Environment;

/**
 * @author: hexploy
 * @date: 03.09.14
 */
public class EnvParamValue extends ParameterValue {
    private Map<String, String> data;

    @DataBoundConstructor
    public EnvParamValue(String envName, EnvParamDescriptor descriptor) {
        super("environment", envName);
        data = new HashMap<String, String>();
        data.put("environment", envName);
        Properties properties = searchPropertiesForEnvironment(envName, descriptor);
        for (String name : properties.stringPropertyNames()) {
            data.put(name, properties.getProperty(name));
        }
    }

    @Override
    public VariableResolver<String> createVariableResolver(AbstractBuild<?, ?> build) {
        return new VariableResolver.ByMap<String>(data);
    }

    @Override
    public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
        env.putAll(data);
    }

    private static Properties searchPropertiesForEnvironment(String envName, EnvParamDescriptor descriptor) {
        Properties properties = new Properties();
        try {
            List<Environment> environments = descriptor.getEnvironments();
            for (Environment environment : environments) {
                if (environment.getName().equals(envName)) {
                    properties.load(new StringReader(environment.getProperties()));
                    break;
                }
            }
        } catch (IOException e) {
            //never happened
        }
        return properties;
    }

    @Override
    public String toString() {
        return "EnvParam=" + data.get("environment");
    }
}

