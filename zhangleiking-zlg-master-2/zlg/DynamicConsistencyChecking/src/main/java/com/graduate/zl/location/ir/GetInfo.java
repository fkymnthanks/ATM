package com.graduate.zl.location.ir;

import com.graduate.zl.location.common.LocConfConstant;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取指定目录下所有的包名、类名与方法名
 */
public class GetInfo {

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

    public void init() {
        this.locConf = LocConfConstant.getLocConf();
        this.moduleBlackList = this.locConf.get("module_black_list").split("&");
        this.moduleName = this.locConf.get("target_module");
        projectDirPath = this.locConf.get("projectDirPath");

        String[] packageMid = this.locConf.get("packageMid").split("&");
        StringBuilder sb = new StringBuilder();
        sb.append(projectDirPath+"\\"); //在不同操作系统上是不一致的，可能为/或者\\
        sb.append(this.moduleName);
        for(String str : packageMid) {
            sb.append("\\").append(str);
        }
        this.packageRoot = sb.toString();
    }

    public GetInfo() {
        this.moduleList = new ArrayList<>();
        this.moduleMapPackages = new HashMap<>();
        this.packageMapClazzs = new HashMap<>();
        this.clazzMapMethods = new HashMap<>();
        this.clazzMapInnerClass = new HashMap<>();
        init();
    }

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
                    if(fileName.contains(".java"))
                        packageMapClazzs.get(packagePrefix).add(fileName.substring(0, fileName.length()-5));
                }
            }
        }
    }

    public void getClassMap(String clazzName) {
        if(!clazzMapMethods.containsKey(clazzName)) {
            clazzMapMethods.put(clazzName, new ArrayList<>());
        }
        if(!clazzMapInnerClass.containsKey(clazzName)) {
            clazzMapInnerClass.put(clazzName, new ArrayList<>());
        }
        Class obj = null;
        try {
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

    public void buildMapInfo() {
        getModuleList(new File(projectDirPath));
        getMap(new File(getPackageRoot()), "");
    }

    public static void main(String[] args) {
        GetInfo getInfo = new GetInfo();
        getInfo.buildMapInfo();

        for(String module : getInfo.getModuleList()) {
            System.out.println("module: " + module);
        }

        for(String modulename : getInfo.getModuleMapPackages().keySet()) {
            System.out.println("module["+modulename+"]: ");
            for(String packagename : getInfo.getModuleMapPackages().get(modulename)) {
                System.out.println("packageName: "+packagename);
            }
            System.out.println("--------------------");
        }

        System.out.println("###############################");

        for(String packagename : getInfo.getPackageMapClazzs().keySet()) {
            System.out.println("package["+packagename+"]: ");
            for(String className : getInfo.getPackageMapClazzs().get(packagename)) {
                System.out.println("className: "+className);
                getInfo.getClassMap(packagename+"."+className);
            }
            System.out.println("--------------------");
        }

        System.out.println("###############################");

        for(String className : getInfo.getClazzMapMethods().keySet()) {
            System.out.println("class["+className+"]: ");
            for(String methodName : getInfo.getClazzMapMethods().get(className)) {
                System.out.println("methodName: "+methodName);
            }
            System.out.println("---------------------");
            for(String innerClassName : getInfo.getClazzMapInnerClass().get(className)) {
                System.out.println("innerClassName: "+innerClassName);
            }
            System.out.println("---------------------");
        }
    }
}
