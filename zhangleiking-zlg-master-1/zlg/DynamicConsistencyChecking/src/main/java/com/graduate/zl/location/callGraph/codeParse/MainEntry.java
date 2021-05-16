package com.graduate.zl.location.callGraph.codeParse;

import org.apache.bcel.classfile.ClassParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 */
public class MainEntry {
    public static void main(String[] args) throws FileNotFoundException {
        ClassParser cp;
        try {
//            System.out.println(new File(".").getAbsoluteFile());
            File f = new File("E:\\Projects\\Java\\DynamicConsistencyChecking\\out\\artifacts\\DynamicCheck\\DynamicCheck.jar");


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

                cp = new ClassParser("E:\\Projects\\Java\\DynamicConsistencyChecking\\out\\artifacts\\DynamicCheck\\DynamicCheck.jar", entry.getName());
                ClassVisitor visitor = new ClassVisitor(cp.parse());
                visitor.start();

            }


        } catch (IOException e) {
            System.err.println("Error while processing jar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
