/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ucm.povale.reader;

import es.ucm.povale.assertion.Assertion;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author PoVALE team
 */
public class AssertNode {
    private Assertion assertion;
    private String message;
    private List<AssertNode> children;
    
    public AssertNode(Assertion assertion, String message) {
        this.assertion = assertion;
        this.message = message;
        this.children = new LinkedList();
    }
    
    public Assertion getAssertion(){
        return assertion;
    }
    
    public void setAssertion(Assertion assertion){
        this.assertion = assertion;
    }
    
    public String getMessage(){
        return message;
    }

    public void setMessage(String newMessage){
        this.message = newMessage;
    }
    
    public List<AssertNode> getChildren(){
        return children;
    }
    
    public void addChild(AssertNode child){
        children.add(child);
    }
    
    public boolean isLeaf(){
        return children.isEmpty();
    }
    
}
