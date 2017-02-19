/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ucm.povale.reader;

import java.util.Map;

/**
 *
 * @author Laura Hernando y Daniel Rossetto
 */
public class Var {
    private String label;
    private String name;
    private String description;
    private String type;
    private Map<String,String> parameters;

    public Var(String label, String name, String description, String type, Map<String,String> parameters) {
        this.label = label;
        this.name = name;
        this.description = description;
        this.type = type;
        this.parameters = parameters;
    }

    public String getLabel() {
        return label;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
    
    

}
