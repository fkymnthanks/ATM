package com.graduate.zl.common.util;

import java.util.List;
import java.util.Map;

/**
 * 常用函数
 */
public class CommonFunc {

    /**
     * 返回两个字符串最长公共子串的长度
     * @param str1
     * @param str2
     * @return
     */
    public static int longestCommonSubstring(String str1, String str2) {
        if(str1==null || str2==null) return 0;
        int len1 = str1.length(), len2 = str2.length();
        if(len1==0 || len2==0) return 0;

        int[] topLine = new int[len1], currentLine = new int[len1];
        int maxLen = 0; char ch = ' ';

        for(int i=0; i<len2; i++){
            ch = str2.charAt(i);
            for(int j=0; j<len1; j++){
                if( ch == str1.charAt(j)){
                    if(j==0){
                        currentLine[j] = 1;
                    } else{
                        currentLine[j] = topLine[j-1] + 1;
                    }
                    if(currentLine[j] > maxLen){
                        maxLen = currentLine[j];
                    }
                }
            }
            for(int k=0; k<len1; k++){
                topLine[k] = currentLine[k];
                currentLine[k] = 0;
            }
        }
        return maxLen;
    }

    /**
     * 检查s1与s2是否匹配
     * @param s1
     * @param s2
     * @param matchLevel matchLevel为1，则为字符串严格匹配，即要求s2存在严格存在于s1中；如果为2，则为字符串中只要存在子串匹配即可（例如：SendPing与PingEcho）
     * @return
     */
    public static boolean match(String s1, String s2, int matchLevel) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        if(matchLevel == 1) {
            return s1.contains(s2);
        }else if(matchLevel == 2) {
            if(longestCommonSubstring(s1, s2) > 3)
                return true;
        } else if(matchLevel == 3) {
            return s1.contains(s2) || s2.contains(s1) || longestCommonSubstring(s1, s2) >= 5;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(match("com.atm.rd.server.warner.monitor", "server handler", 3));
    }
}
