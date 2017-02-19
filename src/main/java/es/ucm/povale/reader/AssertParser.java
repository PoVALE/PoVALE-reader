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
import es.ucm.povale.term.Term;

/**
 *
 * @author Laura Hernando y Daniel Rossetto
 */
public class AssertParser {

    public String getMessage(Element el){
        String message = el.getAttribute("msg");
        if(message == "")
            return null;
        return message;
    }
    public Assertion createAssertFalse(Element el) {
        String message = getMessage(el);
        return new AssertFalse(message);
    }

    public Assertion createAssertTrue(Element el) {
        String message = getMessage(el);
        return new AssertTrue(message);
    }

    public Assertion createNotAssert(Element el) {
        XMLParser parser = new XMLParser();
        NodeList nl = el.getChildNodes();
        Assertion assertion = null;
        String message = getMessage(el);
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if (!nl.item(i).getNodeName().equalsIgnoreCase("#text")) {
                    assertion = parser.getAssertion((Element) nl.item(i));
                }
            }
        }
        return new Not(assertion, message);
    }

    public Assertion createAndAssert(Element el) {
        XMLParser parser = new XMLParser();
        List<Assertion> assertions = new LinkedList();
        NodeList nl = el.getChildNodes();
        String message = getMessage(el);
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if (!nl.item(i).getNodeName().equalsIgnoreCase("#text")) {
                    Element e = (Element) nl.item(i);
                    assertions.add(parser.getAssertion(e));
                }
            }
        }
        return new And(assertions, message);
    }

    public Assertion createOrAssert(Element el) {
        //tiene una assertsList con orAssert
        XMLParser parser = new XMLParser();
        List<Assertion> assertions = new LinkedList();
        NodeList nl = el.getChildNodes();
        String message = getMessage(el);
        
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                if (!nl.item(i).getNodeName().equalsIgnoreCase("#text")) {
                    Element e = (Element) nl.item(i);
                    assertions.add(parser.getAssertion(e));
                }
            }
        }
        return new Or(assertions, message);
    }

    public Assertion createEntailAssert(Element el) {
        XMLParser parser = new XMLParser();
        NodeList nodeList = el.getChildNodes();
        Element element = null;
        Assertion leftAssert = null, rightAssert = null;
        String message = getMessage(el);
        
        if (nodeList != null && nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (!nodeList.item(i).getNodeName().equalsIgnoreCase("#text")) {
                    element = (Element) nodeList.item(i);
                    NodeList asserts = element.getChildNodes();
                    if (asserts != null && asserts.getLength() > 0) {
                        for (int j = 0; j < asserts.getLength(); j++) {
                            if (!asserts.item(j).getNodeName().equalsIgnoreCase("#text")) {
                                if (leftAssert == null) {
                                    leftAssert = parser.getAssertion((Element) asserts.item(j));
                                } else {
                                    rightAssert = parser.getAssertion((Element) asserts.item(j));
                                }
                            }
                        }
                    }
                }
            }
        }
        return new Entail(leftAssert, rightAssert, message);
    }

    public Assertion createEqualsAssert(Element el) {
        XMLParser parser = new XMLParser();
        NodeList nodeList = el.getChildNodes();
        Element element = null;
        Term leftTerm = null, rightTerm = null;
        String message = getMessage(el);
       
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
        return new Equals(leftTerm, rightTerm, message);
    }

    public Assertion createExistAssert(Element el) {
        XMLParser parser = new XMLParser();
        NodeList existElements = el.getChildNodes();
        String variable = null;
        Term term = null;
        Assertion assertion = null;
        ArrayList<Integer> existPos = new ArrayList<>();
        String message = getMessage(el);
        
        if (existElements != null && existElements.getLength() > 0) {
            for (int j = 0; j < existElements.getLength(); j++) {
                if (!existElements.item(j).getNodeName().equalsIgnoreCase("#text")) {
                    existPos.add(j);
                }
            }
            variable = existElements.item(existPos.get(0)).getTextContent();
            term = parser.getTerm((Element) existElements.item(existPos.get(1)));
            assertion = parser.getAssertion((Element) existElements.item(existPos.get(2)));
        }

        return new Exist(variable, term, assertion, message);
    }

    public Assertion createExistOneAssert(Element el) {
        XMLParser parser = new XMLParser();
        NodeList existElements = el.getChildNodes();
        String variable = null;
        Term term = null;
        Assertion assertion = null;
        ArrayList<Integer> existPos = new ArrayList<>();
        String message = getMessage(el);
        
        if (existElements != null && existElements.getLength() > 0) {
            for (int j = 0; j < existElements.getLength(); j++) {
                if (!existElements.item(j).getNodeName().equalsIgnoreCase("#text")) {
                    existPos.add(j);
                }
            }
            variable = existElements.item(existPos.get(0)).getTextContent();
            term = parser.getTerm((Element) existElements.item(existPos.get(1)));
            assertion = parser.getAssertion((Element) existElements.item(existPos.get(2)));
        }
        return new ExistOne(variable, term, assertion, message);
    }

    public Assertion createForAllAssert(Element el) {
        XMLParser parser = new XMLParser();
        NodeList forAllElements = el.getChildNodes();
        String variable = null;
        Term term = null;
        Assertion assertion = null;
        ArrayList<Integer> existPos = new ArrayList<>();
        String message = getMessage(el);
        
        if (forAllElements != null && forAllElements.getLength() > 0) {
            for (int j = 0; j < forAllElements.getLength(); j++) {
                if (!forAllElements.item(j).getNodeName().equalsIgnoreCase("#text")) {
                    existPos.add(j);
                }
            }
            variable = forAllElements.item(existPos.get(0)).getTextContent();
            term = parser.getTerm((Element) forAllElements.item(existPos.get(1)));
            assertion = parser.getAssertion((Element) forAllElements.item(existPos.get(2)));
        }
        return new ForAll(variable, term, assertion, message);
    }

    public Assertion createPredicateApplication(Element el) {
        XMLParser parser = new XMLParser();
        String predicate = null;
        List<Term> predicateTerms = new LinkedList();
        NodeList nodeList = el.getChildNodes();
        String message = getMessage(el);
        
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
        return new PredicateApplication(predicate, predicateTerms, message);
    }

}
