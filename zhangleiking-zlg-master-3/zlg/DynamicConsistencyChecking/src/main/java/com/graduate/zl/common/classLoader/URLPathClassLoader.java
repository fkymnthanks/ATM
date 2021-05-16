package com.graduate.zl.common.classLoader;

import com.graduate.zl.traceability.common.LocConfConstant;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 加载外部的class文件
 */
public class URLPathClassLoader{

    private Map<String, String> locConf;

    private File root;

    private URL[] urls;

    private ClassLoader classLoader;

    private void init() {
        this.locConf = LocConfConstant.getLocConf();
        int proCase = Integer.parseInt(this.locConf.get("proCase"));
        if(proCase == 1) {
            this.root = new File(this.locConf.get("checkATMProjectClassPath"));
        } else if(proCase == 2) {
            this.root = new File(this.locConf.get("checkOMHProjectClassPath"));
        }
        try {
            this.urls = new URL[]{this.root.toURI().toURL()};
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        this.classLoader = new URLClassLoader(this.urls);
    }

    public URLPathClassLoader() {
        init();
    }

    public List<String> getMethodList(String className) {
        List<String> ret = null;
        try {
            Class cls = classLoader.loadClass(className);//com.atm.rd.client.Client$inner
            Method[] methods = cls.getDeclaredMethods();
            if(methods != null) {
                ret = new ArrayList<>();
                for(Method method : methods) {
                    ret.add(method.getName());
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public List<String> getInnerClassList(String className) {
        List<String> ret = null;
        try {
            Class cls = classLoader.loadClass(className);//com.atm.rd.client.Client$inner
            Class[] classes = cls.getDeclaredClasses();
            if(classes != null) {
                ret = new ArrayList<>();
                for(Class clazz : classes) {
                    ret.add(clazz.getName());
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static void main(String[] args) {
//        System.out.println(pk.startsWith("com.atm.rd.client.Client"));
        File file = new File("E:\\Projects\\Java\\ATM\\RegisterDiscovery\\target\\classes");
        try {
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};
            ClassLoader cl = new URLClassLoader(urls);
            Class cls = cl.loadClass("com.atm.rd.client.Client");
            System.out.println(cls.getName());
            Method[] methods = cls.getDeclaredMethods();
            for(Method method : methods) {
                System.out.println(method.getName());
            }
            Class[] classes = cls.getDeclaredClasses();
            for(Class cl1 : classes) {
                System.out.println(cl1.getName());
            }
            Class cls1 = cl.loadClass("com.atm.rd.client.Client$inner");
            Method[] methods1 = cls1.getDeclaredMethods();
            for(Method method : methods1) {
                System.out.println(method.getName());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
