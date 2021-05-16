package com.graduate.zl.location.callGraph;

import com.graduate.zl.sd2Lts.common.TransformConstant;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Log预处理
 */
public class LogHandler {

    //private static String logFullPath = LocConfConstant.getLocConf().get("logFullPath");
    private static String logDirPath;

    private static String logFullPath;

    private static Map<String, String> conf;

    private static Map<String, List<Integer>> contentMapLineNumber = new HashMap<>();

    static {
        conf = TransformConstant.getTransformConf();
        logFullPath = conf.get("logFullPath") + conf.get("originalLogName");
        logDirPath = conf.get("logFullPath");
    }

    /**
     * 获取程序执行一次，打印的Log信息行数
     * @return
     */
    private static int getOnePathNumber() {
        try {
            FileReader reader = new FileReader(logFullPath);
            BufferedReader br = new BufferedReader(reader);
            String str = null;
            int lineNumber = 0;
            while((str = br.readLine()) != null) {
                if(str.trim().length()==0)
                    continue;
                StringBuilder sb = new StringBuilder();
                String[] strs = str.split(";");
                sb.append(strs[0]).append(strs[1]).append(strs[3]);
                String symbol = MD5(sb.toString());
                if(!contentMapLineNumber.containsKey(symbol)) {
                    ArrayList<Integer> list = new ArrayList<>();
                    contentMapLineNumber.put(symbol, list);
                }
                contentMapLineNumber.get(symbol).add(lineNumber);
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int MIN = Integer.MAX_VALUE;
        for(String key : contentMapLineNumber.keySet()) {
            if(contentMapLineNumber.get(key).size()<MIN) {
                MIN = contentMapLineNumber.get(key).size();
            }
        }
        int ret = -1;
        for(String key : contentMapLineNumber.keySet()) {
            if(contentMapLineNumber.get(key).size()==MIN && contentMapLineNumber.get(key).size()>2) {
                ret = contentMapLineNumber.get(key).get(2)-contentMapLineNumber.get(key).get(0);
            }
        }
        return ret;
    }

    /**
     * 创建预处理后的Log文件，并返回文件路径
     * @return
     */
    public static String preHandleLog() {
        StringBuilder sb = new StringBuilder();
        sb.append(logDirPath).append(conf.get("handledLogName"));
        File preHandleLog = new File(sb.toString());
        int cnt = getOnePathNumber();
        String line = null;
        FileReader reader = null;
        FileOutputStream out = null;
        BufferedReader br = null;
        try {
            if(!preHandleLog.exists()) {
                preHandleLog.createNewFile();
                //先清空该文件的内容，方便后面的写入不被其他内容干扰
                FileWriter writer = new FileWriter(preHandleLog);
                writer.write("");
                writer.flush();
                writer.close();
            }
            out = new FileOutputStream(preHandleLog, true);
            reader = new FileReader(logFullPath);
            br = new BufferedReader(reader);
            for(int i=0;i<cnt;i++) {
                StringBuilder csb = new StringBuilder();
                csb.append(br.readLine()).append("\r\n");
                out.write(csb.toString().getBytes("utf-8"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static String MD5(String input){
        MessageDigest md5 = null;
        byte[] out = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            out = md5.digest(input.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bytesToHex(out);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(preHandleLog());
    }
}
