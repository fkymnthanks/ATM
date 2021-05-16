package com.graduate.zl.traceability.ir;

import com.graduate.zl.common.util.CommonFunc;
import com.graduate.zl.sd2Lts.common.TransformConstant;
import com.graduate.zl.sd2Lts.model.SeqDiagram.Message;
import com.graduate.zl.sd2Lts.parse.ParseXmi;
import com.graduate.zl.traceability.callGraph.handle.CallDistance;
import com.graduate.zl.traceability.common.LocConfConstant;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * 获取制定项目的包名、类名、方法名
 */
public class InformationRetrieval {

    private String[] keyATSWords; //可靠性策略关键词

    @Getter @Setter
    //依靠可靠性策略检测出的项目相关包路径
    private Map<String, List<String>> atsRelatedPackage;

    @Getter @Setter
    //依靠可靠性策略检测出的项目相关类名
    private Map<String, List<String>> atsRelatedClass;

    @Getter @Setter
    //依靠可靠性策略检测出的项目相关方法名
    private Map<String, List<String>> atsRelatedMethod;

    @Getter @Setter
    //依靠模型对象名称检测出的项目相关包路径
    private Map<String, List<String>> modelObjRelatedPackage;

    @Getter @Setter
    //依靠模型对象名称检测出的项目相关类名
    private Map<String, List<String>> modelObjRelatedClass;

    @Getter @Setter
    //依靠模型对象名称检测出的项目相关方法名
    private Map<String, List<String>> modelObjRelatedMethod;

    @Getter @Setter
    //依靠模型中的消息名称检测出的项目相关包路径
    private Map<String, List<String>> modelMsgRelatedPackage;

    @Getter @Setter
    //依靠模型模型中的消息名称检测出的项目相关类名
    private Map<String, List<String>> modelMsgRelatedClass;

    @Getter @Setter
    //依靠模型模型中的消息名称检测出的项目相关方法名
    private Map<String, List<String>> modelMsgRelatedMethod;

    private Map<String, String> locConf;

    private Map<String, String> transConf;

    private int matchLevel;

    @Getter
    private ModelInfo modelInfo;

    @Getter
    private CodeInfo codeInfo;

    @Getter
    private Set<String> resultClass;

    private CallDistance cd;

    private String sdXMIPath;

    private ParseXmi parseXmi;

    @Getter
    //模型中元素发起的消息
    private Map<String, List<String>> modelObjRelatedMsg;

    public void init() {
        this.locConf = LocConfConstant.getLocConf();
        this.transConf = TransformConstant.getTransformConf();
        int proCase = Integer.parseInt(this.locConf.get("proCase"));
        this.matchLevel = Integer.parseInt(this.locConf.get("match_level"));
        if(proCase == 1) {
            this.keyATSWords = this.locConf.get("keyATMWords").split("&");
            this.sdXMIPath = this.transConf.get("ATMSequenceDiagramXmiPath") + this.transConf.get("ATMSequenceDiagramXmiName");
        } else if(proCase == 2) {
            this.keyATSWords = this.locConf.get("keyOMHWords").split("&");
            this.sdXMIPath = this.transConf.get("OMHSequenceDiagramXmiPath") + this.transConf.get("OMHSequenceDiagramXmiName");
        }

        this.modelInfo = ModelInfo.getInstance();
        this.codeInfo = CodeInfo.getInstance();

        this.atsRelatedPackage = new HashMap<>();
        this.atsRelatedClass = new HashMap<>();
        this.atsRelatedMethod = new HashMap<>();
        this.modelObjRelatedPackage = new HashMap<>();
        this.modelObjRelatedClass = new HashMap<>();
        this.modelObjRelatedMethod = new HashMap<>();
        this.modelMsgRelatedPackage = new HashMap<>();
        this.modelMsgRelatedClass = new HashMap<>();
        this.modelMsgRelatedMethod = new HashMap<>();
        this.resultClass = new HashSet<>();

        this.cd = CallDistance.getInstance();

        this.parseXmi = new ParseXmi(this.sdXMIPath);
        this.modelObjRelatedMsg = new HashMap<>();
        setObjectRelatedMessage();
    }

    private InformationRetrieval() {
        init();
        executeIR();
        setResultClass();
    }

    private static class InformationRetrievalInstance{
        private static final InformationRetrieval INSTANCE = new InformationRetrieval();
    }

    public static InformationRetrieval getInstance() {
        return InformationRetrievalInstance.INSTANCE;
    }

    /**
     * 设置模型对象与其对应的消息名称（即消息的sender）
     */
    private void setObjectRelatedMessage() {
        this.parseXmi.parseXmi();
        for(Message msg : this.parseXmi.getSequenceDiagram().getMessageList()) {
            String sender = msg.getSenderOrReceiverName(this.parseXmi.getSequenceDiagram(), true);
            if(!this.getModelObjRelatedMsg().containsKey(sender)) {
                this.getModelObjRelatedMsg().put(sender, new ArrayList<>());
            }
            this.getModelObjRelatedMsg().get(sender).add(msg.getName());
        }
    }

    /**
     * 获取输入消息的所属对象名称
     * 由于可能存在消息同名，因此返回一个List；但通常只有一个目标对象
     * @param msgName
     * @return
     */
    private List<String> getMsgBelongedObj(String msgName) {
        List<String> ret = null;
        for(String objName : this.modelObjRelatedMsg.keySet()) {
            List<String> msgList = this.modelObjRelatedMsg.get(objName);
            for(String tmpMsgName : msgList) {
                if(msgName.equals(tmpMsgName)) {
                    if(ret == null) {
                        ret = new ArrayList<>();
                    }
                    ret.add(objName);
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * 执行信息检索
     */
    public void executeIR() {
        for(String moduleName : codeInfo.getModuleMapPackages().keySet()) {
            List<String> packageNames = codeInfo.getModuleMapPackages().get(moduleName);
            for(String pkName : packageNames) {
                String packageName = pkName.toLowerCase();
                for(String kaw : this.keyATSWords) {
                    String keyATWord = kaw.toLowerCase();
                    if(packageName.contains(keyATWord)) {
                        if(!this.atsRelatedPackage.containsKey(keyATWord)) {
                            this.atsRelatedPackage.put(kaw, new ArrayList<>());
                        }
                        boolean needPut = true;
                        for(int i=0;i<this.atsRelatedPackage.get(keyATWord).size();i++) {
                            String str = this.atsRelatedPackage.get(keyATWord).get(i);
                            if(packageName.indexOf(str) == 0) {
                                needPut = false;
                                break;
                            }else if(str.indexOf(packageName) == 0) {
                                this.atsRelatedPackage.get(kaw).remove(str);
                                i--;
                            }
                        }
                        if(needPut) {
                            this.atsRelatedPackage.get(kaw).add(packageName);
                        }
                    }
                }
                for(String mon : this.modelInfo.getObjectNameList()) {
                    String modelObjName = mon.toLowerCase();
                    if(CommonFunc.match(packageName, modelObjName, this.matchLevel)) {
                        if(!this.modelObjRelatedPackage.containsKey(modelObjName)) {
                            this.modelObjRelatedPackage.put(mon, new ArrayList<>());
                        }
                        this.modelObjRelatedPackage.get(mon).add(packageName);
                    }
                }
                for(String mmn : this.modelInfo.getMessageNameList()) {
                    String modelMsgName = mmn.toLowerCase();
                    if(CommonFunc.match(packageName, modelMsgName, this.matchLevel)) {
                        if(!this.modelMsgRelatedPackage.containsKey(modelMsgName)) {
                            this.modelMsgRelatedPackage.put(mmn, new ArrayList<>());
                        }
                        this.modelMsgRelatedPackage.get(mmn).add(packageName);
                    }
                }
            }
        }

        for(String packageName : codeInfo.getPackageMapClazzs().keySet()) {
            List<String> classNames = codeInfo.getPackageMapClazzs().get(packageName);
            for(String cn : classNames) {
                String className = cn.toLowerCase();
                String fullClassName = packageName+"."+cn;
                for(String kaw : this.keyATSWords) {
                    String keyATWord = kaw.toLowerCase();
                    // if(className.toLowerCase().contains(keyATWord.toLowerCase()))
                    if(CommonFunc.match(className, keyATWord, this.matchLevel)) {
                        if(!this.atsRelatedClass.containsKey(keyATWord)) {
                            this.atsRelatedClass.put(kaw, new ArrayList<>());
                        }
                        this.atsRelatedClass.get(kaw).add(fullClassName);
                    }
                }
                for(String mon : modelInfo.getObjectNameList()) {
                    String modelObjectName = mon.toLowerCase();
                    if(CommonFunc.match(className, modelObjectName, this.matchLevel)) {
                        if(!this.modelObjRelatedClass.containsKey(mon)) {
                            this.modelObjRelatedClass.put(mon, new ArrayList<>());
                        }
                        this.modelObjRelatedClass.get(mon).add(fullClassName);
                    }
                }
                for(String mmn : modelInfo.getMessageNameList()) {
                    String modelMessageName = mmn.toLowerCase();
                    if(CommonFunc.match(className, modelMessageName, this.matchLevel)) {
                        if(!this.modelMsgRelatedClass.containsKey(mmn)) {
                            this.modelMsgRelatedClass.put(mmn, new ArrayList<>());
                        }
                        this.modelMsgRelatedClass.get(mmn).add(fullClassName);
                    }
                }

                if(codeInfo.getClazzMapMethods().containsKey(fullClassName)) {
                    List<String> methodNames = codeInfo.getClazzMapMethods().get(fullClassName);
                    for(String mn : methodNames) {
                        String methodName = mn.toLowerCase();
                        for(String kaw : this.keyATSWords) {
                            String keyATWord = kaw.toLowerCase();
                            if(CommonFunc.match(methodName, keyATWord, this.matchLevel)) {
                                if(!this.atsRelatedMethod.containsKey(kaw)) {
                                    this.atsRelatedMethod.put(kaw, new ArrayList<>());
                                }
                                this.atsRelatedMethod.get(kaw).add(fullClassName+":"+mn);
                            }
                        }
                        for(String mon : modelInfo.getObjectNameList()) {
                            String modelObjectName = mon.toLowerCase();
                            if(CommonFunc.match(methodName, modelObjectName, this.matchLevel)) {
                                if(!this.modelObjRelatedMethod.containsKey(mon)) {
                                    this.modelObjRelatedMethod.put(mon, new ArrayList<>());
                                }
                                this.modelObjRelatedMethod.get(mon).add(fullClassName+":"+mn);
                            }
                        }
                        for(String mmn : modelInfo.getMessageNameList()) {
                            String modelMessageName = mmn.toLowerCase();
                            if(CommonFunc.match(methodName, modelMessageName, this.matchLevel)) {
                                if(!this.modelMsgRelatedMethod.containsKey(mmn)) {
                                    this.modelMsgRelatedMethod.put(mmn, new ArrayList<>());
                                }
                                this.modelMsgRelatedMethod.get(mmn).add(fullClassName+":"+mn);
                            }
                        }
                    }
                }
            }
        }
//        processByCG();
    }

    /**
     * 只对符合关键字匹配的方法执行Call Graph，而并非对符合的类中所有的方法执行Call Graph
     */
    private void processByCG() {
        Set<String> handled = new HashSet<>();
        for(String key : this.atsRelatedMethod.keySet()) {
            List<String> relatedMethods = this.atsRelatedMethod.get(key);
            for(String relatedMethod : relatedMethods) {
                if(!handled.contains(relatedMethod)) {
                    handled.add(relatedMethod);
                    List<String> cdRelatedMethods = this.cd.getRelatedMethodsForLocation(relatedMethod);
                    if(cdRelatedMethods != null) {
                        for(String cdRelatedMethod : cdRelatedMethods) {
                            String tt = cdRelatedMethod.split(":")[0];
                            if(!this.resultClass.contains(tt)) {
                                this.resultClass.add(tt);
                                //System.out.println("ATS keyWord: " + key + ", code class: " + tt);
                            }
                        }
                    }
                }
            }
        }
        for(String key : this.modelObjRelatedMethod.keySet()) {
            List<String> relatedMethods = this.modelObjRelatedMethod.get(key);
            for(String relatedMethod : relatedMethods) {
                if(!handled.contains(relatedMethod)) {
                    handled.add(relatedMethod);
                    List<String> cdRelatedMethods = this.cd.getRelatedMethodsForLocation(relatedMethod);
                    if(cdRelatedMethods != null) {
                        for(String cdRelatedMethod : cdRelatedMethods) {
                            String tt = cdRelatedMethod.split(":")[0];
                            if(!this.resultClass.contains(tt)) {
                                this.resultClass.add(tt);
                                System.out.println("Model Object keyWord: " + key +", code class: " + tt);
                            }
                        }
                    }
                }
            }
        }
        for(String key : this.modelMsgRelatedMethod.keySet()) {
            List<String> relatedMethods = this.modelMsgRelatedMethod.get(key);
            for(String relatedMethod : relatedMethods) {
                if(!handled.contains(relatedMethod)) {
                    handled.add(relatedMethod);
                    List<String> cdRelatedMethods = this.cd.getRelatedMethodsForLocation(relatedMethod);
                    if(cdRelatedMethods != null) {
                        for(String cdRelatedMethod : cdRelatedMethods) {
                            String tt = cdRelatedMethod.split(":")[0];
                            if(!this.resultClass.contains(tt)) {
                                this.resultClass.add(tt);
                                System.out.println("Model Message keyWord: " + key + ", Model Object: "+getMsgBelongedObj(key).get(0) + ", code class: " + tt);
                            }
                        }
                    }
                }
            }
        }
    }

    private void setResultClass() {
        for(String keyWord : this.atsRelatedPackage.keySet()) {
            List<String> packageNames = this.atsRelatedPackage.get(keyWord);
            if(packageNames != null) {
                for(String packageName : packageNames) {
                    if(codeInfo.getPackageMapClazzs().containsKey(packageName)) {
                        for(String clazz : codeInfo.getPackageMapClazzs().get(packageName)) {
                            this.resultClass.add(packageName+"."+clazz);
                        }
                    }
                }
            }
        }
        for(String keyWord : this.modelObjRelatedPackage.keySet()) {
            List<String> packageNames = this.modelObjRelatedPackage.get(keyWord);
            if(packageNames != null) {
                for(String packageName : packageNames) {
                    if(codeInfo.getPackageMapClazzs().containsKey(packageName)) {
                        for(String clazz : codeInfo.getPackageMapClazzs().get(packageName)) {
                            this.resultClass.add(packageName+"."+clazz);
                        }
                    }
                }
            }
        }
        for(String keyWord : this.modelMsgRelatedPackage.keySet()) {
            List<String> packageNames = this.modelMsgRelatedPackage.get(keyWord);
            if(packageNames != null) {
                for(String packageName : packageNames) {
                    if(codeInfo.getPackageMapClazzs().containsKey(packageName)) {
                        for(String clazz : codeInfo.getPackageMapClazzs().get(packageName)) {
                            this.resultClass.add(packageName+"."+clazz);
                        }
                    }
                }
            }
        }

        for(String keyWord : this.atsRelatedClass.keySet()) {
            List<String> classNames = this.atsRelatedClass.get(keyWord);
            if(classNames != null) {
                for(String className : classNames) {
                    this.resultClass.add(className);
                }
            }
        }
        for(String keyWord : this.modelObjRelatedClass.keySet()) {
            List<String> classNames = this.modelObjRelatedClass.get(keyWord);
            if(classNames != null) {
                for(String className : classNames) {
                    this.resultClass.add(className);
                }
            }
        }
        for(String keyWord : this.modelMsgRelatedClass.keySet()) {
            List<String> classNames = this.modelMsgRelatedClass.get(keyWord);
            if(classNames != null) {
                for(String className : classNames) {
                    this.resultClass.add(className);
                }
            }
        }

        for(String keyWord : this.atsRelatedMethod.keySet()) {
            List<String> methodNames = this.atsRelatedMethod.get(keyWord);
            if(methodNames != null) {
                for(String methodName : methodNames) {
                    this.resultClass.add(methodName.split(":")[0]);
                }
            }
        }
        for(String keyWord : this.modelObjRelatedMethod.keySet()) {
            List<String> methodNames = this.modelObjRelatedMethod.get(keyWord);
            if(methodNames != null) {
                for(String methodName : methodNames) {
                    this.resultClass.add(methodName.split(":")[0]);
                }
            }
        }
        for(String keyWord : this.modelMsgRelatedMethod.keySet()) {
            List<String> methodNames = this.modelMsgRelatedMethod.get(keyWord);
            if(methodNames != null) {
                for(String methodName : methodNames) {
                    this.resultClass.add(methodName.split(":")[0]);
                }
            }
        }
    }

    /**
     * 打印详细结果
     */
    private void printDetailResult() {
        System.out.println("#####-----Package Level-----#####");
        for(String keyWord : this.atsRelatedPackage.keySet()) {
            System.out.println("<ATS keyWord: "+keyWord+">");
            List<String> packageNames = this.atsRelatedPackage.get(keyWord);
            if(packageNames != null) {
                for(String packageName : packageNames) {
                    System.out.println(packageName);
                }
            }else {
                System.out.println("none related!!!");
            }
        }
        for(String keyWord : this.modelObjRelatedPackage.keySet()) {
            System.out.println("<Model Object keyWord: "+keyWord+">");
            List<String> packageNames = this.modelObjRelatedPackage.get(keyWord);
            if(packageNames != null) {
                for(String packageName : packageNames) {
                    System.out.println(packageName);
                }
            }else {
                System.out.println("none related!!!");
            }
        }
        for(String keyWord : this.modelMsgRelatedPackage.keySet()) {
            System.out.println("<Model Message keyWord: "+keyWord+", belonged to "+ getMsgBelongedObj(keyWord)+">");
            List<String> packageNames = this.modelMsgRelatedPackage.get(keyWord);
            if(packageNames != null) {
                for(String packageName : packageNames) {
                    System.out.println(packageName);
                }
            }else {
                System.out.println("none related!!!");
            }
        }
        System.out.println("#####-----Package Level-----#####");
        System.out.println("");

        System.out.println("#####-----Class Level-----#####");
        for(String keyWord : this.atsRelatedClass.keySet()) {
            System.out.println("<ATS keyWord: "+keyWord+">");
            List<String> classNames = this.atsRelatedClass.get(keyWord);
            if(classNames != null) {
                for(String className : classNames) {
                    System.out.println(className);
                }
            }else {
                System.out.println("none related!!!");
            }
        }
        for(String keyWord : this.modelObjRelatedClass.keySet()) {
            System.out.println("<Model Object keyWord: "+keyWord+">");
            List<String> classNames = this.modelObjRelatedClass.get(keyWord);
            if(classNames != null) {
                for(String className : classNames) {
                    System.out.println(className);
                }
            }else {
                System.out.println("none related!!!");
            }
        }
        for(String keyWord : this.modelMsgRelatedClass.keySet()) {
            System.out.println("<Model Message keyWord: "+keyWord+", belonged to "+ getMsgBelongedObj(keyWord)+">");
            List<String> classNames = this.modelMsgRelatedClass.get(keyWord);
            if(classNames != null) {
                for(String className : classNames) {
                    System.out.println(className);
                }
            }else {
                System.out.println("none related!!!");
            }
        }
        System.out.println("#####-----Class Level-----#####");
        System.out.println("");

        System.out.println("#####-----Method Level-----#####");
        for(String keyWord : this.atsRelatedMethod.keySet()) {
            System.out.println("<ATS keyWord: "+keyWord+">");
            List<String> methodNames = this.atsRelatedMethod.get(keyWord);
            if(methodNames != null) {
                for(String methodName : methodNames) {
                    System.out.println(methodName);
                }
            }else {
                System.out.println("none related!!!");
            }
        }
        for(String keyWord : this.modelObjRelatedMethod.keySet()) {
            System.out.println("<Model Object keyWord: "+keyWord+">");
            List<String> methodNames = this.modelObjRelatedMethod.get(keyWord);
            if(methodNames != null) {
                for(String methodName : methodNames) {
                    System.out.println(methodName);
                }
            }else {
                System.out.println("none related!!!");
            }
        }
        for(String keyWord : this.modelMsgRelatedMethod.keySet()) {
            System.out.println("<Model Message keyWord: "+keyWord+", belonged to "+ getMsgBelongedObj(keyWord)+">");
            List<String> methodNames = this.modelMsgRelatedMethod.get(keyWord);
            if(methodNames != null) {
                for(String methodName : methodNames) {
                    System.out.println(methodName);
                }
            }else {
                System.out.println("none related!!!");
            }
        }
        System.out.println("#####-----Method Level-----#####");
    }

    private void printResultClass() {
        System.out.println("*****.....printResultClass().....*****");
        for(String clazz : this.getResultClass()) {
            System.out.println(clazz);
        }
    }

    private void printCodeInfo() {
        for(String packageName : this.codeInfo.getPackageMapClazzs().keySet()) {
            System.out.println("packageName: "+packageName);
            for(String className : this.codeInfo.getPackageMapClazzs().get(packageName)) {
                System.out.println(className);
            }
        }
        for(String className : this.codeInfo.getClazzMapMethods().keySet()) {
            System.out.println("className: "+className);
            for(String methodName : this.codeInfo.getClazzMapMethods().get(className)) {
                System.out.println(methodName);
            }
        }
    }

    public static void main(String[] args) {
        InformationRetrieval ir = new InformationRetrieval();

        ir.printDetailResult();
        ir.printResultClass();
//        ir.printCodeInfo();
    }
}
