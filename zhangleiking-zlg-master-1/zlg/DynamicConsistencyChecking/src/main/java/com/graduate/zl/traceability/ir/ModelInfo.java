package com.graduate.zl.traceability.ir;

import com.graduate.zl.sd2Lts.common.Constants;
import com.graduate.zl.sd2Lts.common.TransformConstant;
import com.graduate.zl.traceability.common.LocConfConstant;
import lombok.Getter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取UML时序图模型中的相关信息
 * 主要的文本信息包括对象名称、消息名称
 */
public class ModelInfo {

    @Getter
    private List<String> objectNameList;

    @Getter
    private List<String> messageNameList;

    private String modelFileFullPath;

    private Map<String, String> transConf;

    private Map<String, String> locConf;

    private Map<String, List<String>> modelObjMappingMsg;

    private void init() {
        this.objectNameList = new ArrayList<>();
        this.messageNameList = new ArrayList<>();
        this.modelObjMappingMsg = new HashMap<>();
        this.transConf = TransformConstant.getTransformConf();
        this.locConf = LocConfConstant.getLocConf();
        int proCase = Integer.parseInt(this.locConf.get("proCase"));
        if(proCase == 1) {
            modelFileFullPath = this.transConf.get("ATMSequenceDiagramXmiPath") + this.transConf.get("ATMSequenceDiagramXmiName");
        } else if(proCase == 2) {
            modelFileFullPath = this.transConf.get("OMHSequenceDiagramXmiPath") + this.transConf.get("OMHSequenceDiagramXmiName");
        }
    }

    private static class ModelInfoInstance{
        private static final ModelInfo INSTANCE = new ModelInfo();
    }

    public static ModelInfo getInstance() {
        return ModelInfoInstance.INSTANCE;
    }

    private ModelInfo() {
        init();
        parseInfo();
    }

    /**
     * 提取UML时序图中的lifeline和message
     */
    public void parseInfo() {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(new File(this.modelFileFullPath));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = document.getRootElement();
        List<Element> firstLevel = root.elements();
        Element UML_MODEL = firstLevel.get(1);
        Element ownedBehavior = (Element) UML_MODEL.elements().get(0);
        while(ownedBehavior.getName().equals("packagedElement"))
            ownedBehavior = (Element) ownedBehavior.elements().get(0);
        List<Element> allElements = ownedBehavior.elements();
        for(Element element : allElements) {
            String elementName = element.getName();
            if (elementName.equals(Constants.LIFELINE)) {
                String name = element.attribute("name").getValue();
                this.objectNameList.add(name);
            } else if(elementName.equals(Constants.MESSAGE)) {
                String name = element.attribute("name").getValue();
                this.messageNameList.add(name);
            }
        }
    }

    public static void main(String[] args) {
        ModelInfo gm = ModelInfo.getInstance();
        for(String objectName: gm.getObjectNameList())
            System.out.println(objectName);
        for(String msgName : gm.getMessageNameList())
            System.out.println(msgName);
    }
}
