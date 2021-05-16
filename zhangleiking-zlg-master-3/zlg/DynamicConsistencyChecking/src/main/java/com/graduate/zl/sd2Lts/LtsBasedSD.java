package com.graduate.zl.sd2Lts;

import com.graduate.zl.common.model.Lts.LTS;
import com.graduate.zl.common.model.LtsPath.LTSNodePath;
import com.graduate.zl.common.util.LtsUtil;
import com.graduate.zl.sd2Lts.common.TransformConstant;
import com.graduate.zl.sd2Lts.transform.TransformSd2Lts;
import com.graduate.zl.traceability.common.LocConfConstant;

import java.util.List;
import java.util.Map;

/**
 * 将时序图转换为LTS的主入口
 */
public class LtsBasedSD {

    private static Map<String, String> transConf = null;

    private static Map<String, String> locConf = null;

    private static String sequenceDiagramFullPath;

    private static void init() {
        transConf = TransformConstant.getTransformConf();
        locConf = LocConfConstant.getLocConf();
        int proCase = Integer.parseInt(locConf.get("proCase"));
        if(proCase == 1) {
            sequenceDiagramFullPath = transConf.get("ATMSequenceDiagramXmiPath") + transConf.get("ATMSequenceDiagramXmiName");
        } else if(proCase == 2) {
            sequenceDiagramFullPath = transConf.get("OMHSequenceDiagramXmiPath") + transConf.get("OMHSequenceDiagramXmiName");
        }
    }

    public static LTS process() {
        init();

        TransformSd2Lts trans = new TransformSd2Lts(sequenceDiagramFullPath);
        LTS ret = trans.getLTS();
        return ret;
    }

    public static void main(String[] args) {
        LTS lts = process();
        List<List<LTSNodePath>> allPaths = LtsUtil.getAllPath(lts.getStart());
        LtsUtil.printAllPath(allPaths);
    }

}
