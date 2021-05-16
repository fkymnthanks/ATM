package com.graduate.zl.location.ir;

import com.graduate.zl.sd2Lts.common.Constants;
import com.graduate.zl.sd2Lts.common.TransformConstant;
import lombok.Getter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取UML时序图模型中的相关信息
 * 主要的文本信息包括对象名称、消息名称
 */
public class GetModelInfo {

    @Getter
    private List<String> objectNameList;

    @Getter
    private List<String> messageNameList;

    private String modelFileFullPath;

    private Map<String, String> conf;

    private void init() {
        this.objectNameList = new ArrayList<>();
        this.messageNameList = new ArrayList<>();
        this.conf = TransformConstant.getTransformConf();
        this.modelFileFullPath = this.conf.get("sequenceDiagramXmiPath") + this.conf.get("sequenceDiagramXmiName") + ".xml";
    }

    public GetModelInfo() {
        init();
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
        GetModelInfo gm = new GetModelInfo();
        gm.parseInfo();
        for(String objectName: gm.getObjectNameList())
            System.out.println(objectName);
        for(String msgName : gm.getMessageNameList())
            System.out.println(msgName);
    }
}
