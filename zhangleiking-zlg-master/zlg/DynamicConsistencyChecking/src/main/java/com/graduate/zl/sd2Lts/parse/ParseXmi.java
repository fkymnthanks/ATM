package com.graduate.zl.sd2Lts.parse;

import com.graduate.zl.sd2Lts.common.Constants;
import com.graduate.zl.sd2Lts.model.SeqDiagram.*;
import lombok.Getter;
import lombok.Setter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

/**
 * 解析XMI文件
 */
public class ParseXmi {

    private String fileName;

    private Document document;

    private Map<String, Lifeline> lifelines;

    private Map<String,  OccurrenceSpecificationFragment> osFragments;

    private Map<String, CombinedFragment> combinedFragments;

    private Map<String, InteractionOperand> interactionOperands;

    private Map<String, Message> messageMap;

    private List<Message> messageList;

    @Getter @Setter
    private SequenceDiagram sequenceDiagram;

    public ParseXmi(String fileName) {
        this.fileName = fileName;
        this.document = loadDocument();
        this.lifelines = new HashMap<>();
        this.messageMap = new HashMap<>();
        this.messageList = new ArrayList<>();
        this.osFragments = new HashMap<>();
        this.combinedFragments = new HashMap<>();
        this.interactionOperands = new HashMap<>();
        this.sequenceDiagram = new SequenceDiagram();
    }

    public Document loadDocument() {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(new File(this.fileName));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 解析UML SD的xmi文件
     */
    public void parseXmi() {
        Element root = this.document.getRootElement();
        List<Element> firstLevel = root.elements();
        Element UML_MODEL = firstLevel.get(1);
        Element ownedBehavior = (Element) UML_MODEL.elements().get(0);
        while(ownedBehavior.getName().equals("packagedElement"))
            ownedBehavior = (Element) ownedBehavior.elements().get(0);
        List<Element> allElements = ownedBehavior.elements();
        for(Element element : allElements) {
            String elementName = element.getName();
            if (elementName.equals(Constants.LIFELINE)) {
                Lifeline lifeline = new Lifeline(element.attribute("id").getValue(), element.attribute("name").getValue());
                this.lifelines.put(lifeline.getId(), lifeline);
            } else if(elementName.equals(Constants.FRAGMENT)) {
                String type = element.attribute("type").getValue();
                if(type.equals(Constants.OCCURRENCE_SPECIFICATION)) {
                    String osId = element.attribute("id").getValue();
                    OccurrenceSpecificationFragment osf = new OccurrenceSpecificationFragment(osId, element.attribute("covered").getValue());
                    this.osFragments.put(osId, osf);
                }else if(type.equals(Constants.COMBINED_FRAGMENT)){
                    parseCombinedFragment(element);
                }
            } else if(elementName.equals(Constants.MESSAGE)) {
                String mid = element.attribute("id").getValue();
                Message message = new Message(mid, element.attribute("name").getValue(), element.attribute("sendEvent").getValue(), element.attribute("receiveEvent").getValue());
                this.messageMap.put(mid, message);
                this.messageList.add(message);
            }
        }
        setSequenceDiagram();
    }

    /**
     * 解析Combined Fragment
     * CF包含InteractionOperand
     * @param cf
     */
    private void parseCombinedFragment(Element cf) {
        CombinedFragment ret = new CombinedFragment();

        String cfId = cf.attribute("id").getValue();
        ret.setId(cfId);
        ret.setName(cf.attribute("name").getValue());
        ret.setType(cf.attribute("interactionOperator").getValue());
        List<InteractionOperand> retOperands = new ArrayList<>();

        List<Element> cfElements = cf.elements();
        for(Element element : cfElements) {
            String elementName = element.getName();
            if(elementName.equals(Constants.OPERAND)) {
                InteractionOperand operand = new InteractionOperand();
                String iaoId = element.attribute("id").getValue();
                operand.setId(iaoId);
                operand.setBelongCombinedFragmentId(cfId);
                List<OccurrenceSpecificationFragment> operandCFs = new ArrayList<>();
                List<Element> opeElements = element.elements();
                for(Element element1 : opeElements) {
                    String elementName1 = element1.getName();
                    if(elementName1.equals(Constants.GUARD)) {
                        String guardId = element1.attribute("id").getValue();
                        String guardBody = "";
                        Element guardOpaqueExpression = (Element)(element1.elements().get(0));
                        if(guardOpaqueExpression.attribute("body") != null) {
                            guardBody = guardOpaqueExpression.attribute("body").getValue();
                        }
                        Guard guard = new Guard(guardId, guardBody);
                        operand.setGuard(guard);
                    }else if(elementName1.equals(Constants.FRAGMENT)) {
                        String osId = element1.attribute("id").getValue();
                        OccurrenceSpecificationFragment osf = new OccurrenceSpecificationFragment(osId, element1.attribute("covered").getValue());
                        osf.setBelongInteractionOperandId(iaoId); //设置operand中的OccurrenceSpecification所属operandId
                        osf.setBelongToCF(true);
                        this.osFragments.put(osId, osf);
                        operandCFs.add(osf);
                    }
                }
                operand.setOsFragments(operandCFs);
                this.interactionOperands.put(iaoId, operand);
                retOperands.add(operand);
            }
        }
        ret.setOperandList(retOperands);
        this.combinedFragments.put(cfId, ret);
    }

    private void setSequenceDiagram() {
        this.sequenceDiagram.setLifelines(this.lifelines);
        this.sequenceDiagram.setOsFragments(this.osFragments);
        this.sequenceDiagram.setCombinedFragments(this.combinedFragments);
        this.sequenceDiagram.setMessageMap(this.messageMap);
        this.sequenceDiagram.setMessageList(this.messageList);
        this.sequenceDiagram.setInteractionOperands(this.interactionOperands);
    }

    public static void main(String[] args) {
        ParseXmi parseXmi = new ParseXmi("E:\\tmp\\xml\\Test\\CFTestBreak.xml");
        parseXmi.parseXmi();
        System.out.println(parseXmi.getSequenceDiagram().getMessageList().size());
    }
}
