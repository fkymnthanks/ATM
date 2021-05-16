package com.graduate.zl.traceability.callGraph.handle;

import com.graduate.zl.traceability.common.LocConfConstant;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 计算方法调用之间的“距离”
 */
public class CallDistance {

    private static final int INF = 99999;

    @Getter
    private int[][] distance;

    @Getter
    private PreHandleCG preHandleCG;

    private int methodNodeNum;

    private Map<String, String> conf;

    private int locationValidCallDistance;

    private int mappingValidCallDistance;

    private void init() {
        preHandleCG = new PreHandleCG();
        this.methodNodeNum = preHandleCG.getMethodCallNodes().size();
        this.distance = new int[this.methodNodeNum+1][this.methodNodeNum+1];
        this.conf = LocConfConstant.getLocConf();
        this.locationValidCallDistance = Integer.parseInt(this.conf.get("locationValidCallDistance"));
        this.mappingValidCallDistance = Integer.parseInt(this.conf.get("mappingValidCallDistance"));
        arrayInit();
    }

    private void arrayInit() {
        for(int i=1; i<=this.methodNodeNum; i++) {
            for(int j=1; j<=this.methodNodeNum; j++) {
                this.distance[i][j] = i==j ? 0 : INF;
            }
        }
        for(String methodName : this.preHandleCG.getMethodCallMap().keySet()) {
            int num1 = getNum(methodName);
            List<String> connectedMethods = this.preHandleCG.getMethodCallMap().get(methodName);
            for(String connectedMethodName : connectedMethods) {
                int num2 = getNum(connectedMethodName);
                this.distance[num1][num2] = 1;
            }
        }
    }

    /**
     * 获取输入方法名的对应数字序号
     * @param methodName
     * @return
     */
    private int getNum(String methodName) {
        int ret = -1;
        for(String mn : this.preHandleCG.getMethodCallNodes().keySet()) {
            if(methodName.equals(mn)) {
                ret = this.preHandleCG.getMethodCallNodes().get(methodName);
                break;
            }
        }
        return ret;
    }

    /**
     * 与上述方法相反，获取输入数字序号对应的方法名
     * @param num
     * @return
     */
    private String getMethodName(int num) {
        String ret = null;
        for(String mn : this.preHandleCG.getMethodCallNodes().keySet()) {
            if(this.preHandleCG.getMethodCallNodes().get(mn)==num) {
                ret = mn;
                break;
            }
        }
        return ret;
    }

    private static class CallDistanceInstance{
        private static final CallDistance INSTANCE = new CallDistance();
    }

    public static CallDistance getInstance() {
        return CallDistanceInstance.INSTANCE;
    }

    private CallDistance() {
        init();
        calculateDistance();
    }

    /**
     * 计算方法间距离
     */
    public void calculateDistance() {
        for(int k=1; k<=this.methodNodeNum; k++) {
            for(int i=1; i<=this.methodNodeNum; i++) {
                for(int j=1; j<=this.methodNodeNum; j++) {
                    if(this.distance[i][j] > this.distance[i][k] + this.distance[k][j]) {
                        this.distance[i][j] = this.distance[i][k] + this.distance[k][j];
                    }
                }
            }
        }
    }

    /**
     * 针对location过程，获取有效调用距离内的相关调用方法
     * @param method com.atm.rd.model.Response:getContent(class:method)
     * @return
     */
    public List<String> getRelatedMethodsForLocation(String method) {
        List<String> ret = new ArrayList<>();
        if(!this.preHandleCG.getMethodCallNodes().containsKey(method)) {
            System.out.println("Not include this method in methodCallLog: "+ method);
            return null;
        }
        int num1 = this.preHandleCG.getMethodCallNodes().get(method);
        for(int i=1; i<=this.methodNodeNum; i++) {
            if(i != num1 && this.distance[num1][i] < this.locationValidCallDistance) {
                ret.add(getMethodName(i));
            }
        }
        return ret;
    }

    /**
     * 针对mapping过程，获取有效调用距离内的相关调用方法
     * @param method com.atm.rd.model.Response:getContent(class:method)
     * @return
     */
    public List<String> getRelatedMethodsForMapping(String method) {
        List<String> ret = new ArrayList<>();
        if(!this.preHandleCG.getMethodCallNodes().containsKey(method))
            return null;
        int num1 = this.preHandleCG.getMethodCallNodes().get(method);
        for(int i=1; i<=this.methodNodeNum; i++) {
            if(i != num1 && this.distance[num1][i] < this.mappingValidCallDistance) {
                ret.add(getMethodName(i));
            }
        }
        return ret;
    }

    public static void main(String[] args) {
        CallDistance cd = CallDistance.getInstance();
        for(int i=1;i<cd.getDistance().length;i++) {
            for(int j=1;j<cd.getDistance().length;j++) {
                System.out.print(cd.getDistance()[i][j]+" ");
            }
            System.out.println("");
        }
        System.out.println("-------------------------");
        List<String> re = cd.getRelatedMethodsForMapping("com.atm.rd.server.service.FaultService:handleException");
        for(String mm : re) {
            System.out.println(mm);
        }
    }
}
