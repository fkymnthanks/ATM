package com.graduate.zl.location.callGraph.codeParse;

import com.graduate.zl.location.common.LocConfConstant;
import org.apache.bcel.classfile.ClassParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * call graph 入口
 */
public class CallGraphMainEntry {

    private Map<String, String> locConf;

    private String jarFullPath;

    private void init() {
        this.locConf = LocConfConstant.getLocConf();
        this.jarFullPath = locConf.get("checkProjectJarPath")+locConf.get("checkProjectJarName");
    }

    public CallGraphMainEntry() {
        init();
    }

    public void process() {
        ClassParser cp;
        try {
            File f = new File(jarFullPath);

            if (!f.exists()) {
                System.err.println("Jar file " + "DynamicCheck.jar" + " does not exist");
            }

            JarFile jar = new JarFile(f);
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory())
                    continue;

                if (!entry.getName().endsWith(".class"))
                    continue;

                cp = new ClassParser(jarFullPath, entry.getName());
                ClassVisitor visitor = new ClassVisitor(cp.parse());
                visitor.start();
            }
        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws FileNotFoundException {
        CallGraphMainEntry cg = new CallGraphMainEntry();
        cg.process();
    }
}
