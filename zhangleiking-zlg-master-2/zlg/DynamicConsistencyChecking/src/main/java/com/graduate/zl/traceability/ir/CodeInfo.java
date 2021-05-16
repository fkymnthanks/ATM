package com.graduate.zl.traceability.ir;

import com.graduate.zl.common.classLoader.URLPathClassLoader;
import com.graduate.zl.traceability.common.LocConfConstant;
import lombok.Getter;
import lombok.Setter;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取指定目录下所有的包名、类名与方法名
 */
public class CodeInfo {

    private static String projectDirPath;

    @Getter @Setter
    //存储所有module名称的列表
    private List<String> moduleList;

    @Getter @Setter
    //存储每个module下所有的package名称
    private Map<String, List<String>> moduleMapPackages;

    @Getter @Setter
    //存储每个package下所有的class名称
    private Map<String, List<String>> packageMapClazzs;

    @Getter @Setter
    //存储每个class里所有方法的名称（这里的class包括public class和inner class）
    private Map<String, List<String>> clazzMapMethods;

    @Getter @Setter
    //存储每个class里所有的内部类名称
    private Map<String, List<String>> clazzMapInnerClass;

    private Map<String, String> locConf;

    //module黑名单列表，用于过滤不需要的module
    private String[] moduleBlackList;

    @Getter @Setter
    //指定的module名称
    private String moduleName;

    @Getter @Setter
    private String packageRoot;

    private URLPathClassLoader urlPathClassLoader;

    public void init() {
        this.urlPathClassLoader = new URLPathClassLoader();
        this.locConf = LocConfConstant.getLocConf();
        this.moduleBlackList = this.locConf.get("module_black_list").split("&");
        this.moduleName = this.locConf.get("target_module");
        int proCase = Integer.parseInt(this.locConf.get("proCase"));
        if(proCase == 1) {
            projectDirPath = this.locConf.get("ATMProjectDirPath");
        } else if(proCase == 2) {
            projectDirPath = this.locConf.get("OMHProjectDirPath");
        }

        String[] packageMid = this.locConf.get("packageMid").split("&");
        StringBuilder sb = new StringBuilder();
        sb.append(projectDirPath+"\\"); //在不同操作系统上是不一致的，可能为/或者\\
        if(fileExist(sb.toString()+this.moduleName)) {
            sb.append(this.moduleName);
        } else {
            this.moduleName = "src";
        }
        for(String str : packageMid) {
            sb.append("\\").append(str);
        }
        this.packageRoot = sb.toString();

        this.moduleList = new ArrayList<>();
        this.moduleMapPackages = new HashMap<>();
        this.packageMapClazzs = new HashMap<>();
        this.clazzMapMethods = new HashMap<>();
        this.clazzMapInnerClass = new HashMap<>();
    }

    private static class CodeInfoInstance{
        private static final CodeInfo INSTANCE = new CodeInfo();
    }

    public static CodeInfo getInstance() {
        return CodeInfoInstance.INSTANCE;
    }

    private CodeInfo() {
        init();
        buildMapInfo();
    }

    /**
     * 输入路径，判断该路径下的文件是否存在
     * @param path
     * @return
     */
    private boolean  fileExist(String path) {
        File tmp = new File(path);
        if(!tmp.exists()) {
            return false;
        }
        return true;
    }

    /**
     * 获取文件夹下的所有文件列表
     * @param folder
     */
    public void getModuleList(File folder) {
        File[] files = folder.listFiles();
        for(int i=0, size = files.length; i<size; i++) {
            File file = files[i];
            if(file.isDirectory()) {
                String fileName = file.getName();
                boolean exist = false;
                for(String blackName : this.moduleBlackList) {
                    if(!fileName.contains(blackName.trim())) {
                        exist = true;
                        break;
                    }
                }
                if(!exist) {
                    moduleList.add(file.getName());
                }
            }
        }
    }

    /**
     * 获取模块下对应的包以及包下对应的类
     * @param folder
     * @param packagePrefix
     */
    public void getMap(File folder, String packagePrefix) {
        if(!moduleMapPackages.containsKey(this.moduleName)) {
            moduleMapPackages.put(this.moduleName, new ArrayList<>());
        }

        File[] files = folder.listFiles();
        String tmp = null;
        for(int i=0, size = files.length; i<size; i++) {
            File file = files[i];
            if(file.isDirectory()) {
                if (packagePrefix==null || packagePrefix.length()==0) {
                    tmp = packagePrefix + file.getName();
                    getMap(file, tmp);
                    moduleMapPackages.get(moduleName).add(tmp);
                } else {
                    tmp = packagePrefix + "." + file.getName();
                    getMap(file, packagePrefix + "." + file.getName());
                    moduleMapPackages.get(moduleName).add(tmp);
                }
            } else {
                if(packagePrefix.length() > 0) {
                    if(!packageMapClazzs.containsKey(packagePrefix)) {
                        packageMapClazzs.put(packagePrefix, new ArrayList<>());
                    }
                    String fileName = file.getName();
                    if(fileName.contains(".java")) {
                        String fileNameWtPostfix = fileName.substring(0, fileName.length()-5);
                        packageMapClazzs.get(packagePrefix).add(fileNameWtPostfix);
                        getClassMap1(packagePrefix+"."+fileNameWtPostfix);
                    }
                }
            }
        }
    }

    /**
     * 获取类对应的内部类以及方法
     * @param clazzName
     */
    private void getClassMap1(String clazzName) {
        if(!this.clazzMapMethods.containsKey(clazzName)) {
            this.clazzMapMethods.put(clazzName, new ArrayList<>());
        }
        if(!this.clazzMapInnerClass.containsKey(clazzName)) {
            this.clazzMapInnerClass.put(clazzName, new ArrayList<>());
        }

        List<String> methodList = this.urlPathClassLoader.getMethodList(clazzName);
        List<String> innerClassList = this.urlPathClassLoader.getInnerClassList(clazzName);
        if(methodList != null) {
            for(String methodName : methodList) {
                this.clazzMapMethods.get(clazzName).add(methodName);
            }
        }
        if(innerClassList != null) {
            for(String className : innerClassList) {
                this.clazzMapInnerClass.get(clazzName).add(className);
                getClassMap1(className);
            }
        }
    }

    private void getClassMap(String clazzName) {
        if(!clazzMapMethods.containsKey(clazzName)) {
            clazzMapMethods.put(clazzName, new ArrayList<>());
        }
        if(!clazzMapInnerClass.containsKey(clazzName)) {
            clazzMapInnerClass.put(clazzName, new ArrayList<>());
        }
        Class obj = null;
        try {
            System.out.println("getClassMap: "+clazzName);
            obj = Class.forName(clazzName);
            Method[] methods = obj.getDeclaredMethods();
            Class[] classes = obj.getDeclaredClasses();
            for(Method method : methods) {
                clazzMapMethods.get(clazzName).add(method.getName());
            }
            for(Class clazz : classes) {
                clazzMapInnerClass.get(clazzName).add(clazz.getName());
                getClassMap(clazz.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 该类的重要方法，集成步骤
     */
    public void buildMapInfo() {
        getModuleList(new File(projectDirPath));
        getMap(new File(getPackageRoot()), "");
    }

    /**
     * 代码信息打印
     */
    public void printInfo() {
        for(String moduleName : this.getModuleMapPackages().keySet()) {
            System.out.println("moduleName: "+moduleName);
            for(String packageName : this.getModuleMapPackages().get(moduleName)) {
                System.out.println(packageName);
            }
        }
        for(String packageName : this.getPackageMapClazzs().keySet()) {
            System.out.println("packageName: "+packageName);
            for(String className : this.getPackageMapClazzs().get(packageName)) {
                System.out.println(className);
            }
        }
        for(String className : this.getClazzMapMethods().keySet()) {
            System.out.println("className: "+className);
            for(String methodName : this.getClazzMapMethods().get(className)) {
                System.out.println(methodName);
            }
        }
        for(String className : this.getClazzMapInnerClass().keySet()) {
            System.out.println("className: "+className);
            for(String innerClassName : this.getClazzMapInnerClass().get(className)) {
                System.out.println(innerClassName);
            }
        }
    }

    public void printAllClass() {
        for(String packageName : this.getPackageMapClazzs().keySet()) {
            for(String className : this.getPackageMapClazzs().get(packageName)) {
                System.out.println(packageName+"."+className);
            }
        }
        for(String className : this.getClazzMapInnerClass().keySet()) {
            for(String innerClassName : this.getClazzMapInnerClass().get(className)) {
                System.out.println(innerClassName);
            }
        }
    }

    public static void main(String[] args) {
        CodeInfo getInfo = CodeInfo.getInstance();
        getInfo.printInfo();
//        getInfo.printAllClass();
    }
}
