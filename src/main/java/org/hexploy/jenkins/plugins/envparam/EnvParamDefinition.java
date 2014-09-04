package org.hexploy.jenkins.plugins.envparam;

import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: hexploy
 * @date: 03.09.14
 */
public class EnvParamDefinition extends ParameterDefinition {
    @DataBoundConstructor
    public EnvParamDefinition() {
        super("environment", "");
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jsonObject) {
        String env = req.getParameter("environment");
        return new EnvParamValue(env, (EnvParamDescriptor) getDescriptor());
    }

    @Override
    public ParameterValue createValue(StaplerRequest req) {
        String env = req.getParameter("environment");
        return new EnvParamValue(env, (EnvParamDescriptor) getDescriptor());
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
