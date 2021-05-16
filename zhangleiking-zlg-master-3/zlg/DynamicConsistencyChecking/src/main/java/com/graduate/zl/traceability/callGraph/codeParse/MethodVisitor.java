package com.graduate.zl.traceability.callGraph.codeParse;

import com.graduate.zl.traceability.common.LocConfConstant;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;


/**
 * parse method in class and output call info
 */
public class MethodVisitor extends EmptyVisitor  {
    private static final Logger logger = LoggerFactory.getLogger(MethodVisitor.class);

    JavaClass visitedClass;
    private MethodGen mg;
    private ConstantPoolGen cp;
    private String format;
    private String DegreeClass;
    private String DegreeMethod;

    private String methodCallFilePath;

    private Map<String, String> locConf;

    private void init() {
        this.locConf = LocConfConstant.getLocConf();
        int proCase = Integer.parseInt(this.locConf.get("proCase"));
        if(proCase == 1) {
            this.methodCallFilePath = this.locConf.get("methodCallFilePath") + this.locConf.get("methodCallFileNameOfATM");
        } else if(proCase == 2) {
            this.methodCallFilePath = this.locConf.get("methodCallFilePath") + this.locConf.get("methodCallFileNameOfOMH");
        }
        createFile();
    }

    private void createFile() {
        File file = new File(this.methodCallFilePath);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.write("");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MethodVisitor(MethodGen m, JavaClass jc) {
        visitedClass = jc;
        mg = m;
        cp = mg.getConstantPool();
        init();
    }

    public void start() {
        if (mg.isAbstract() || mg.isNative())
            return;
        for (InstructionHandle ih = mg.getInstructionList().getStart();
             ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();

            if (!visitInstruction(i))
                i.accept(this);
        }
    }

    private boolean visitInstruction(Instruction i) {
        short opcode = i.getOpcode();
        return ((InstructionConstants.INSTRUCTIONS[opcode] != null)
                && !(i instanceof ConstantPushInstruction)
                && !(i instanceof ReturnInstruction));
    }

    @Override
    public void visitINVOKEVIRTUAL(INVOKEVIRTUAL i) {
        String formatInternal = "%s";
        this.DegreeClass = String.format(formatInternal,i.getReferenceType(cp));
        this.DegreeMethod = i.getMethodName(cp);
        String content = visitedClass.getClassName() + ":" + mg.getName() + " CALL " + this.DegreeClass + ":" + this.DegreeMethod;
        logger.info(content);
    }

}
