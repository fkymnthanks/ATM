package com.graduate.zl.log2Lts;

import com.graduate.zl.common.model.Lts.LTS;
import com.graduate.zl.common.util.LtsUtil;
import com.graduate.zl.traceability.callGraph.LogHandler;
import com.graduate.zl.log2Lts.transform.BuildLtsFromLog;

/**
 * 使用Log构建LTS的主入口
 */
public class LtsBasedLog {
    /**
     * 整个处理过程
     * @return
     */
    public static LTS process() {
        LTS ret = null;
        //先预处理log
        LogHandler.preHandleLog();
        //接着利用处理后的Log构建LTS
        BuildLtsFromLog blfl = new BuildLtsFromLog();
        ret = blfl.getLTS();
        return ret;
    }

    public static void main(String[] args) {
        //如果该操作能打印出一条路径，则证明成功
        LtsUtil.printAllPath(LtsUtil.getAllPath(process().getStart()));
    }
}
