package com.graduate.zl.CaseCheck;

import com.graduate.zl.CaseCheck.ATM.CheckATM;
import com.graduate.zl.CaseCheck.OMH.CheckOMH;

import java.util.Scanner;

/**
 * check主入口
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入选择：（1 — ATM PingEcho; 2 — OMH Authorize）: ");
        int n = scanner.nextInt();
        if(n == 1) {
            CheckATM.checkPingEcho();
        }else if(n == 2) {
            CheckOMH.checkAuthorize();
        } else {
            System.out.println("输入有误！");
        }
    }
}
