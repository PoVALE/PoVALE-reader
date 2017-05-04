/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ucm.povale.reader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import es.ucm.povale.assertion.And;
import es.ucm.povale.assertion.AssertFalse;
import es.ucm.povale.assertion.AssertTrue;
import es.ucm.povale.assertion.Assertion;
import es.ucm.povale.assertion.Entail;
import es.ucm.povale.assertion.Equals;
import es.ucm.povale.assertion.Exist;
import es.ucm.povale.assertion.ExistOne;
import es.ucm.povale.assertion.ForAll;
import es.ucm.povale.assertion.Not;
import es.ucm.povale.assertion.Or;
import es.ucm.povale.assertion.PredicateApplication;
import es.ucm.povale.environment.Environment;
import es.ucm.povale.function.Function;
import es.ucm.povale.predicate.Predicate;
import es.ucm.povale.term.FunctionApplication;
import es.ucm.povale.term.Term;

/**
 *
 * @author Laura Hernando y Daniel Rossetto
 */
public class AssertParser {

    public String getMessage(Element el){
        String message = el.getAttribute("msg");
        if(message.equals(""))
            return null;
        return message;
    }
    
    public AssertNode createAssertFalse(List<Object> list) {
        Element el = (Element)list.get(0);
        String message = getMessage(el);
        Assertion assertion = new AssertFalse(message);
        if(message == null){
            message = "Nunca se cumple esta condicion";
        }
        return new AssertNode(assertion, message);
    }

    public AssertNode createAssertTrue(List<Object> list) {
        Element el = (Element)list.get(0);
        String message = getMessage(el);
        Assertion assertion = new AssertTrue(message);
        if(message == null){
            message = "Siempre se cumple esta condicion";
        }
        return new AssertNode(assertion, message);
    }

    public AssertNode createNotAssert(List<Object> list) {
        Element el = (Element)list.get(0);
        Environment env = (Environment) list.get(1);
        XMLParser parser = new XMLParser();
        NodeList nl = el.getChildNodes();
        String message = getMessage(el);

        AssertNode notNode = new AssertNode(null,message);
        if(message == null){
            notNode.setMessage("No se debe cumplir la siguiente condicion:");
        }
        AssertNode child; 
        Assertion notAssertion;
        
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if (!nl.item(i).getNodeName().equalsIgnoreCase("#text")) {
                    child = parser.getAssertion((Element) nl.item(i), env);
                    notAssertion = new Not(child.getAssertion(), message);
                    notNode.setAssertion(notAssertion);
                    notNode.addChild(child);
                }
            }
        }
        return notNode;
    }

    public AssertNode createAndAssert(List<Object> list) {
        Element el = (Element)list.get(0);
        Environment env = (Environment) list.get(1);
        XMLParser parser = new XMLParser();
        NodeList nl = el.getChildNodes();
        String message = getMessage(el);
        AssertNode andNode = new AssertNode(null,message);
        AssertNode child; 
        Assertion andAssertion;
        List<Assertion> assertions = new LinkedList();
        
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if (!nl.item(i).getNodeName().equalsIgnoreCase("#text")) {
                    Element e = (Element) nl.item(i);
                    child = parser.getAssertion(e, env);
                    assertions.add(child.getAssertion());
                    andNode.addChild(child);
                }
            }
        }
        
        andAssertion = new And(assertions, message);
        andNode.setAssertion(andAssertion);
        if(message == null){
            andNode.setMessage("Se deben cumplir las siguientes condiciones:");
        }
        return andNode;
    }

    public AssertNode createOrAssert(List<Object> list) {
        Element el = (Element)list.get(0);
        Environment env = (Environment) list.get(1);
        XMLParser parser = new XMLParser();
        NodeList nl = el.getChildNodes();
        String message = getMessage(el);
        AssertNode orNode = new AssertNode(null,message);
        AssertNode child; 
        Assertion orAssertion;
        List<Assertion> assertions = new LinkedList();
        
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if (!nl.item(i).getNodeName().equalsIgnoreCase("#text")) {
                    Element e = (Element) nl.item(i);
                    child = parser.getAssertion(e, env);
                    assertions.add(child.getAssertion());
                    orNode.addChild(child);
                }
            }
        }
        
        orAssertion = new Or(assertions, message);
        orNode.setAssertion(orAssertion);
        if(message == null){
            orNode.setMessage("Debe cumplirse al menos una de las siguientes condiciones:");
        }
        return orNode;
    }

    public AssertNode createEntailAssert(List<Object> list) {
        Element el = (Element)list.get(0);
        Environment env = (Environment) list.get(1);
        XMLParser parser = new XMLParser();
        NodeList nodeList = el.getChildNodes();
        Element element = null;
        Assertion leftAssert = null, rightAssert = null;
        String message = getMessage(el);
        AssertNode entailNode = new AssertNode(null,message);
        AssertNode child; 
        Assertion entailAssertion;
        
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (!nodeList.item(i).getNodeName().equalsIgnoreCase("#text")) {
                    element = (Element) nodeList.item(i);
                    NodeList asserts = element.getChildNodes();
                    if (asserts != null && asserts.getLength() > 0) {
                        for (int j = 0; j < asserts.getLength(); j++) {
                            if (!asserts.item(j).getNodeName().equalsIgnoreCase("#text")) {
                                if (leftAssert == null) {
                                    child = parser.getAssertion((Element) asserts.item(j), env);
                                    leftAssert = child.getAssertion();
                                    entailNode.addChild(child);
                                } else {
                                    child = parser.getAssertion((Element) asserts.item(j), env);
                                    rightAssert = child.getAssertion();
                                    entailNode.addChild(child);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        entailAssertion = new Entail(leftAssert, rightAssert, message);
        entailNode.setAssertion(entailAssertion);
        if(message == null){
            entailNode.setMessage(entailNode.getChildren().get(0).getMessage() + " implica a " 
                    + entailNode.getChildren().get(1).getMessage());
        }
        return entailNode;
    }

    public AssertNode createEqualsAssert(List<Object> list) {
        Element el = (Element)list.get(0);
        Environment env = (Environment) list.get(1);
        XMLParser parser = new XMLParser();
        NodeList nodeList = el.getChildNodes();
        Element element = null;
        Term leftTerm = null, rightTerm = null;
        String message = getMessage(el);
        AssertNode equalsNode = new AssertNode(null,message); 
        Assertion equalsAssertion;
       
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (!nodeList.item(i).getNodeName().equalsIgnoreCase("#text")) {
                    element = (Element) nodeList.item(i);
                    NodeList terms = element.getChildNodes();
                    if (terms != null && terms.getLength() > 0) {
                        for (int j = 0; j < terms.getLength(); j++) {
                            if (!terms.item(j).getNodeName().equalsIgnoreCase("#text")) {
                                if (leftTerm == null) {
                                    leftTerm = parser.getTerm((Element) terms.item(j));
                                } else {
                                    rightTerm = parser.getTerm((Element) terms.item(j));
                                }
                            }
                        }
                    }
                }
            }
        }
        equalsAssertion = new Equals(leftTerm, rightTerm, message);
        equalsNode.setAssertion(equalsAssertion);
        if(message == null){
            String lTerm = getStringTerm(leftTerm, env);
            String rTerm = getStringTerm(rightTerm, env);
            equalsNode.setMessage(lTerm + " es igual a " + rTerm);
        }
        return equalsNode;
    }

    public AssertNode createExistAssert(List<Object> list) {
        Element el = (Element)list.get(0);
        Environment env = (Environment) list.get(1);
        XMLParser parser = new XMLParser();
        NodeList existElements = el.getChildNodes();
        String variable = null;
        Term term = null;
        Assertion assertion = null;
        ArrayList<Integer> existPos = new ArrayList<>();
        String message = getMessage(el);
        AssertNode existNode = new AssertNode(null,message);
        AssertNode child; 
        Assertion existAssertion;
        
        if (existElements != null && existElements.getLength() > 0) {
            for (int i = 0; i < existElements.getLength(); i++) {
                if (!existElements.item(i).getNodeName().equalsIgnoreCase("#text")) {
                    existPos.add(i);
                }
            }
            variable = existElements.item(existPos.get(0)).getTextContent();
            term = parser.getTerm((Element) existElements.item(existPos.get(1)));
            child = parser.getAssertion((Element) existElements.item(existPos.get(2)), env);
            assertion = child.getAssertion();
            existNode.addChild(child);
        }
        
        existAssertion = new Exist(variable, term, assertion, message);
        existNode.setAssertion(existAssertion);
        if(message == null){
            String stringTerm = getStringTerm(term, env);
            existNode.setMessage("Existe un elemento " + variable + " en " + stringTerm +
                " tal que cumple: ");
        }
        return existNode;
    }

    public AssertNode createExistOneAssert(List<Object> list) {
        Element el = (Element)list.get(0);
        Environment env = (Environment) list.get(1);
        XMLParser parser = new XMLParser();
        NodeList existElements = el.getChildNodes();
        String variable = null;
        Term term = null;
        Assertion assertion = null;
        ArrayList<Integer> existPos = new ArrayList<>();
        String message = getMessage(el);
        AssertNode existOneNode = new AssertNode(null,message);
        AssertNode child; 
        Assertion existOneAssertion;
        
        if (existElements != null && existElements.getLength() > 0) {
            for (int i = 0; i < existElements.getLength(); i++) {
                if (!existElements.item(i).getNodeName().equalsIgnoreCase("#text")) {
                    existPos.add(i);
                }
            }
            variable = existElements.item(existPos.get(0)).getTextContent();
            term = parser.getTerm((Element) existElements.item(existPos.get(1)));
            child = parser.getAssertion((Element) existElements.item(existPos.get(2)), env);
            assertion = child.getAssertion();
            existOneNode.addChild(child);
        }
        existOneAssertion = new ExistOne(variable, term, assertion, message);
        existOneNode.setAssertion(existOneAssertion);
        
        if(message == null){
            String stringTerm = getStringTerm(term, env);
            existOneNode.setMessage("Existe solo un elemento " + variable + " en " + stringTerm +
                " tal que cumple: ");
        }
        return existOneNode;
    }

    public AssertNode createForAllAssert(List<Object> list) {
        Element el = (Element)list.get(0);
        Environment env = (Environment) list.get(1);
        XMLParser parser = new XMLParser();
        NodeList forAllElements = el.getChildNodes();
        String variable = null;
        Term term = null;
        Assertion assertion = null;
        ArrayList<Integer> existPos = new ArrayList<>();
        String message = getMessage(el);
        AssertNode forAllNode = new AssertNode(null,message);
        AssertNode child; 
        Assertion forAllAssertion;
        
        if (forAllElements != null && forAllElements.getLength() > 0) {
            for (int j = 0; j < forAllElements.getLength(); j++) {
                if (!forAllElements.item(j).getNodeName().equalsIgnoreCase("#text")) {
                    existPos.add(j);
                }
            }
            variable = forAllElements.item(existPos.get(0)).getTextContent();
            term = parser.getTerm((Element) forAllElements.item(existPos.get(1)));
            child = parser.getAssertion((Element) forAllElements.item(existPos.get(2)), env);
            assertion = child.getAssertion();
            forAllNode.addChild(child);
        }
        
        forAllAssertion = new ForAll(variable, term, assertion, message);
        forAllNode.setAssertion(forAllAssertion);

        if(message == null){
            String stringTerm = getStringTerm(term, env);
            forAllNode.setMessage("Para todo elemento " + variable + " en " + stringTerm +
                " cumple: ");
        }
        return forAllNode;
    }

    public AssertNode createPredicateApplication(List<Object> list) {
        Element el = (Element)list.get(0);
        Environment env = (Environment) list.get(1);
        XMLParser parser = new XMLParser();
        String predicate = null;
        List<Term> predicateTerms = new LinkedList();
        NodeList nodeList = el.getChildNodes();
        String message = getMessage(el);
        AssertNode predicateApplicationNode = new AssertNode(null,message);
        Assertion predicateApplicationAssertion;
        
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (!nodeList.item(i).getNodeName().equalsIgnoreCase("#text")) {
                    if (predicate == null) {
                        predicate = nodeList.item(i).getTextContent();
                    } else {
                        NodeList termList = nodeList.item(i).getChildNodes();
                        if (termList != null && termList.getLength() > 0) {
                            for (int j = 0; j < termList.getLength(); j++) {
                                if (!termList.item(j).getNodeName().equalsIgnoreCase("#text")) {
                                    predicateTerms.add(parser.getTerm((Element) termList.item(j)));
                                }
                            }
                        }
                    }
                }
            }
        }
        
        predicateApplicationAssertion = new PredicateApplication(predicate, predicateTerms, message);
        predicateApplicationNode.setAssertion(predicateApplicationAssertion);
        if(message == null){
            Predicate p = env.getPredicate(predicate);
            message="";
            for(Term t: predicateTerms){
                if(predicateTerms.size()==1){
                    message+=predicateTerms.get(0).toString()+" ";
                }
                message+=t.toString()+", ";
            }
            String aux[] = p.getParamDescriptions();
            for(String s: aux){
                if(s != null){
                    message+=s;
                }
            }
        }
        predicateApplicationNode.setMessage(message);
        return predicateApplicationNode;
    }

    private String getStringTerm(Term term, Environment env) {
        String stringTerm;
        if(term.getClass()!= FunctionApplication.class){
            stringTerm = term.toString();
        }else{
            FunctionApplication aux = (FunctionApplication)term;
            Function f = env.getFunction(aux.getFunction());
            stringTerm="";
            String auxS[] = f.getParamDescriptions();
            for(String s: auxS){
                if(s != null){
                    stringTerm+=s;
                }
            }  
        }
        return stringTerm;
    }

}
