package org.hexploy.jenkins.plugins.envparam;

import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.util.ListBoxModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: hexploy
 * @date: 03.09.14
 */
public class EnvParamDefinition extends ParameterDefinition {
    private static final String ENVIRONMENT_PARAMETER_NAME = "environment";

    private String defaultValue;

    @DataBoundConstructor
    public EnvParamDefinition(String defaultValue) {
        super(ENVIRONMENT_PARAMETER_NAME, "");
        this.defaultValue = defaultValue;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jsonObject) {
        String env = getEnvironmentParameter(req);
        return new EnvParamValue(env, (EnvParamDescriptor) getDescriptor());
    }

    @Override
    public ParameterValue createValue(StaplerRequest req) {
        String env = getEnvironmentParameter(req);
        return new EnvParamValue(env, (EnvParamDescriptor) getDescriptor());
    }

    @Override
    public ParameterValue getDefaultParameterValue() {
        return new EnvParamValue(getCalculatedDefaultValue(), (EnvParamDescriptor) getDescriptor());
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    private String getEnvironmentParameter(StaplerRequest req) {
        String env = req.getParameter("environment");
        if (StringUtils.isEmpty(env)) {
            env = getCalculatedDefaultValue();
        }
        return env;
    }

    private String getCalculatedDefaultValue() {
        if (StringUtils.isEmpty(defaultValue)) {
            return ((EnvParamDescriptor) getDescriptor()).getEnvironments().get(0).getName();
        } else {
            return defaultValue;
        }
    }

    @Extension
    public static class EnvParamDescriptor extends ParameterDescriptor {
        private List<Environment> environments = new ArrayList<Environment>();

        public EnvParamDescriptor() {
            load();
        }

        public EnvParamDescriptor(Class<? extends ParameterDefinition> klazz) {
            super(klazz);
            load();
        }

        public List<Environment> getEnvironments() {
            return environments;
        }

        public void setEnvironments(List<Environment> environments) {
            this.environments = environments;
        }

        @Override
        public String getDisplayName() {
            return "Environment parameter";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            System.out.println("EnvParamDefinition config JSON=" + json);
            JSONArray environmentsJson = JSONArray.fromObject(json.get("environments"));
            List<Environment> environmentsNew = new ArrayList<Environment>();
            for (int i = 0; i < environmentsJson.size(); i++) {
                JSONObject jsonEnv = environmentsJson.getJSONObject(i);
                if (!jsonEnv.isEmpty() && !jsonEnv.isNullObject()) {
                    environmentsNew.add((Environment) jsonEnv.toBean(Environment.class));
                }
            }
            environments = environmentsNew;
            save();
            return true;
        }

        public ListBoxModel doFillDefaultValueItems() {
            ListBoxModel envList = new ListBoxModel();
            for (Environment environment : environments) {
                envList.add(environment.getName());
            }
            return envList;
        }

        public static class Environment {
            private String name;
            private String properties;

            public Environment() {
            }

            public Environment(String name, String properties) {
                setName(name);
                setProperties(properties);
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getProperties() {
                return properties;
            }

            public void setProperties(String properties) {
                this.properties = properties;
            }
        }
    }

}
