/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ucm.povale.reader;

import es.ucm.povale.variable.Var;
import es.ucm.povale.assertInformation.AssertInformation;
import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import es.ucm.povale.assertion.Assertion;
import es.ucm.povale.term.Term;
import java.io.InputStream;

/**
 *
 * @author Laura Hernando y Daniel Rossetto
 */
public class XMLParser {

    private List<Var> myVars;
    private List<String> myPlugins;
    private String rootFile;
    private List<Assertion> myAsserts;
    private Map<String, Function<Element, Term>> termsMap;
    private Map<String, Function<Element, AssertNode>> assertsMap;
    private AssertNode myRequirements;
    private Map<String, String> defaultMessages;
    
    public XMLParser() {

        this.myAsserts = new LinkedList();
        this.myPlugins = new LinkedList();
        this.myVars = new LinkedList();
        myRequirements = new AssertNode(null, "Se deben cumplir los siguientes requisitos:");
        this.rootFile = "";

        this.termsMap = new HashMap<>();
        TermParser termParser = new TermParser();
        termsMap.put("variable", termParser::createVariable);
        termsMap.put("literalString", termParser::createLiteralString);
        termsMap.put("literalInteger", termParser::createLiteralInteger);
        termsMap.put("listTerm", termParser::createListTerm);
        termsMap.put("functionApplication", termParser::createFunctionApplication);

        this.assertsMap = new HashMap<>();
        AssertParser assertParser = new AssertParser();
        assertsMap.put("assertFalse", assertParser::createAssertFalse);
        assertsMap.put("assertTrue", assertParser::createAssertTrue);
        assertsMap.put("not", assertParser::createNotAssert);
        assertsMap.put("and", assertParser::createAndAssert);
        assertsMap.put("or", assertParser::createOrAssert);
        assertsMap.put("entail", assertParser::createEntailAssert);
        assertsMap.put("equals", assertParser::createEqualsAssert);
        assertsMap.put("exist", assertParser::createExistAssert);
        assertsMap.put("existOne", assertParser::createExistOneAssert);
        assertsMap.put("forAll", assertParser::createForAllAssert);
        assertsMap.put("predicateApplication", assertParser::createPredicateApplication);
        
        this.defaultMessages = new HashMap<>();
        defaultMessages.put("assertFalse", "Nunca se cumple esta condicion");
        defaultMessages.put("assertTrue", "Siempre se cumple esta condicion");
        defaultMessages.put("not", "No se debe cumplir la siguiente condicion:");
        defaultMessages.put("and", "Se deben cumplir las siguientes condiciones:");
        defaultMessages.put("or", "Debe cumplirse al menos una de las siguientes condiciones:");
        defaultMessages.put("entail", "El elemento 1 implica al elemento 2");
        defaultMessages.put("equals", "El elemento 1 es igual al elemento 2");
        defaultMessages.put("exist", "Existe un elemento X que cumple:");
        defaultMessages.put("existOne", "Existe solo un elemento X que cumple:");
        defaultMessages.put("forAll", "Para todo elemento X cumple:");
        defaultMessages.put("predicateApplication", "Funcion de predicado...");

    }

    public void parseXMLFile(InputStream is) {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dom = db.parse(is);
            Element document = dom.getDocumentElement();
            readPlugins(document);
            readRootFile(document);
            readVars(document);
            readRootAssertion(document);
        } catch (ParserConfigurationException | SAXException | IOException pce) {
            pce.printStackTrace();
        }
    }

    protected Term getTerm(Element element) {
        String term = element.getTagName();
        Function<Element, Term> termParserFunction = this.termsMap.get(term);
        Term t = termParserFunction.apply(element);
        return t;
    }

    protected AssertNode getAssertion(Element element) {
        String assertion = element.getTagName();
        Function<Element, AssertNode> assertParserFunction = this.assertsMap.get(assertion);
        AssertNode a = assertParserFunction.apply(element);
        if (a.getMessage() == null)
            a.setMessage(this.defaultMessages.get(assertion));
        return a;
    }

    private void readRootAssertion(Element document) {
        NodeList nl = document.getElementsByTagName("assert");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                NodeList nol = nl.item(i).getChildNodes();
                for (int j = 0; j < nol.getLength(); j++) {
                    if (!nol.item(j).getNodeName().equalsIgnoreCase("#text")) {
                        Element el = (Element) nol.item(j);       
                        AssertNode assertNode = getAssertion(el);
                        Assertion a = assertNode.getAssertion();
                        myAsserts.add(a);
                        myRequirements.addChild(assertNode);
                    }
                }
            }
        }
    }

    private void readPlugins(Element document) {
        NodeList nl = document.getElementsByTagName("import");
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                myPlugins.add(nl.item(i).getTextContent());
            }
        }
    }

    private void readVars(Element document) {
        NodeList nl = document.getElementsByTagName("var");
        ArrayList<Integer> var = new ArrayList<>();
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                NodeList nol = nl.item(i).getChildNodes();
                for (int j = 0; j < nol.getLength(); j++) {
                    if (!nol.item(j).getNodeName().equalsIgnoreCase("#text")) {
                        var.add(j);
                    }
                }
                String label = nol.item(var.get(0)).getTextContent();
                String name = nol.item(var.get(1)).getTextContent();
                String description = nol.item(var.get(2)).getTextContent();
                String entityType = nol.item(var.get(3)).getTextContent();
                Map<String,String> parameters = new HashMap<>();
                if(nol.getLength() == 5){
                    NodeList params = nol.item(var.get(4)).getChildNodes();
                        if(params.getLength() > 0)
                        {
                            for(int j = 0 ; j < params.getLength(); i++) 
                            {
                                if(!params.item(i).getNodeName().equalsIgnoreCase("#text")){
                                    Element e = (Element)params.item(i);
                                    String key = e.getTagName();
                                    parameters.put(key, e.getAttribute(key));
                                }
                            }
                        myVars.add(new Var(label, name, description, entityType, parameters));
                        }
                }else{
                    myVars.add(new Var(label, name, description, entityType));
                }
            }
        }
    }

    private void readRootFile(Element document) {

        NodeList nl = document.getElementsByTagName("rootFile");
        if (nl != null && nl.getLength() > 0) {
            this.rootFile = nl.item(0).getTextContent();
        }

    }

    public List<Var> getMyVars() {
        return myVars;
    }

    public List<String> getMyPlugins() {
        return myPlugins;
    }

    public List<Assertion> getMyAsserts() {
        return myAsserts;
    }
    
    public AssertNode getMyRequirements() {
        return myRequirements;
    }

}
