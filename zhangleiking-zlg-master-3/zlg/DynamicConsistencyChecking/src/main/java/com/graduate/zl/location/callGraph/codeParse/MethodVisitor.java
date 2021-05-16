package com.graduate.zl.location.callGraph.codeParse;

import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

    public MethodVisitor(MethodGen m, JavaClass jc) {
        visitedClass = jc;
        mg = m;
        cp = mg.getConstantPool();

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
        logger.info(visitedClass.getClassName() + ":" + mg.getName() + " CALL " + this.DegreeClass + ":" + this.DegreeMethod);
        System.out.println(visitedClass.getClassName() + ":" + mg.getName() + " CALL " + this.DegreeClass + ":" + this.DegreeMethod);
    }

}
