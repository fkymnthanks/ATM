package com.graduate.zl.common.util;

import com.graduate.zl.common.model.Lts.LNode;
import com.graduate.zl.common.model.LtsPath.LTSNodePath;

import java.util.ArrayList;
import java.util.List;

/**
 * 提供针对LTS的工具
 */
public class LtsUtil {

    /**
     * 获取LTS的所有路径
     * @param root
     * @return
     */
    public static List<List<LTSNodePath>> getAllPath(LNode root) {
        List<List<LTSNodePath>> ret = new ArrayList<>();
        dfs(ret, root, new ArrayList<>());
        return ret;
    }

    /**
     * 深度优先搜索获取所有路径
     * @param ret
     * @param node
     * @param sigPath
     */
    private static void dfs(List<List<LTSNodePath>> ret, LNode node, List<LTSNodePath> sigPath) {
        if(node==null) return;

        if(node.getNext().size() == 0) {
            LTSNodePath nt = new LTSNodePath(node, null);
            sigPath.add(nt);

            ret.add(new ArrayList<>(sigPath));
            sigPath.remove(sigPath.size() - 1);
            return;
        }

        for(LNode key : node.getNext().keySet()) {
            LTSNodePath nt = new LTSNodePath(node, node.getNext().get(key));
            sigPath.add(nt);
            dfs(ret, key, sigPath);
            sigPath.remove(sigPath.size() - 1);
        }
    }

    /**
     * 打印LTS中获取所有的路径
     * @param allPath
     */
    public static void printAllPath(List<List<LTSNodePath>> allPath) {
        StringBuilder sb;
        for(List<LTSNodePath> path : allPath) {
            sb = new StringBuilder();
            for(LTSNodePath nt : path) {
                sb.append(nt.getNode().getNumber()+">");
                System.out.println(nt.toString());
            }
            System.out.println(sb.substring(0, sb.length()-1));
            System.out.println("---------------------");
        }
    }

    public static void printPath(List<LTSNodePath> path) {
        StringBuilder sb;
        sb = new StringBuilder();
        for(LTSNodePath nt : path) {
            sb.append(nt.getNode().getNumber()+">");
            System.out.println(nt.toString());
        }
        System.out.println(sb.substring(0, sb.length()-1));
    }

    public static void printSimplePath(List<LTSNodePath> path) {
        StringBuilder sb;
        sb = new StringBuilder();
        for(LTSNodePath nt : path) {
            sb.append(nt.getNode().getNumber()+">");
        }
        System.out.println(sb.substring(0, sb.length()-1));
    }
}
