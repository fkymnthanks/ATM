package com.graduate.zl.traceability.callGraph.codeParse;

import com.graduate.zl.common.util.FileUtil;
import com.graduate.zl.traceability.common.LocConfConstant;
import org.apache.bcel.classfile.ClassParser;

import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * call graph 入口
 */
public class CallGraphMainEntry {

    private Map<String, String> locConf;

    private String jarFullPath;

    private String methodCallLogPath;

    private String copyFilePath;

    private void init() {
        this.locConf = LocConfConstant.getLocConf();
        this.methodCallLogPath = this.locConf.get("methodCallFilePath") + "methodCall.log";
        int proCase = Integer.parseInt(this.locConf.get("proCase"));
        if(proCase == 1) {
            this.jarFullPath = locConf.get("ATMCheckProjectJarPath")+locConf.get("ATMCheckProjectJarName");
            this.copyFilePath = this.locConf.get("methodCallFilePath") + this.locConf.get("methodCallFileNameOfATM");
        } else if(proCase == 2) {
            this.jarFullPath = locConf.get("OMHCheckProjectJarPath")+locConf.get("OMHCheckProjectJarName");
            this.copyFilePath = this.locConf.get("methodCallFilePath") + this.locConf.get("methodCallFileNameOfOMH");
        }
    }

    public CallGraphMainEntry() {
        init();
        process();
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
        copyLogContent();
    }

    private void copyLogContent() {
        File copyFile = new File(this.copyFilePath);
        FileWriter writer = null;
        BufferedWriter bw = null;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            if (!copyFile.exists()) {
                copyFile.createNewFile();
            }
            writer = new FileWriter(copyFile);
            bw = new BufferedWriter(writer);
            fr = new FileReader(this.methodCallLogPath);
            br = new BufferedReader(fr);
            String logContent;
            while ((logContent = br.readLine()) != null) {
                bw.write(logContent+"\r\n");
            }
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            try {
                if(fr != null) {
                    fr.close();
                }
                if(bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileUtil.clearContent(this.methodCallLogPath);
    }


    public static void main(String[] args) throws FileNotFoundException {
        CallGraphMainEntry cg = new CallGraphMainEntry();
    }
}
