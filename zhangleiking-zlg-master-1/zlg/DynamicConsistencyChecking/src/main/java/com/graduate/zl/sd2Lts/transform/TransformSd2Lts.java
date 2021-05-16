package com.graduate.zl.sd2Lts.transform;

import com.graduate.zl.common.model.LtsPath.LTSNodePath;
import com.graduate.zl.common.util.LtsUtil;
import com.graduate.zl.sd2Lts.common.Constants;
import com.graduate.zl.common.model.Lts.LNode;
import com.graduate.zl.common.model.Lts.LTS;
import com.graduate.zl.common.model.Lts.LTransition;
import com.graduate.zl.common.model.Lts.LTransitionLabel;
import com.graduate.zl.sd2Lts.model.SeqDiagram.*;
import com.graduate.zl.sd2Lts.parse.ParseXmi;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 将UML时序图转换为LTS
 */
public class TransformSd2Lts {

    private String sequenceDiagramPath;

    private AtomicInteger count = new AtomicInteger(0);

    private LNode root = null;

    private int skip = 0;

    private LNode LTS_END = null;

    public TransformSd2Lts(String sdFilePath) {
        this.sequenceDiagramPath = sdFilePath;
    }

    public void transform() {
        ParseXmi parseXmi = new ParseXmi(this.sequenceDiagramPath);
        parseXmi.parseXmi();
        SequenceDiagram sd = parseXmi.getSequenceDiagram();
        transform(sd);
    }

    /**
     * 转换处理
     * @param sd
     */
    private void transform(SequenceDiagram sd) {
        LNode pre = root;
        int nextNumber;
        //每一条消息对应两个LNode和一个LTransition，但实际上只用创建一个LNode和一个LTransition（root节点除外）
        for(int k=0, size=sd.getMessageList().size(); k<size; k++) {
            Message message = sd.getMessageList().get(k);
            //如果当前消息属于CF片段
            String[] belongCFAndIO = message.getBelongedCF(sd);
            if(belongCFAndIO[0] != null) {
                CombinedFragment curCF = sd.getCombinedFragments().get(belongCFAndIO[0]);
                String cfType = curCF.getType();
                if(this.root == null) {
                    nextNumber = count.getAndIncrement();
                    LNode firstNode = new LNode(nextNumber, message.getSenderOrReceiverName(sd, true));
                    if (cfType.equals(Constants.CF_TYPE_OPT)) {
                        pre = handleOptCF(firstNode, curCF, sd, k);
                        k += (skip-1);
                    } else if(cfType.equals(Constants.CF_TYPE_ALT)) {
                        pre = handleAltCF(firstNode, curCF, sd, k);
                        k += (skip-1);
                    } else if(cfType.equals(Constants.CF_TYPE_LOOP)) {
                        pre = handleLoopCF(firstNode, curCF, sd, k);
                        k += (skip-1);
                    } else if(cfType.equals(Constants.CF_TYPE_BREAK)) {
                        pre = handleBreakCF(firstNode, curCF, sd, k);
                        k += (skip-1);
                    }
                    skip = 0;
                    this.root = firstNode;
                }else {
                    if (cfType.equals(Constants.CF_TYPE_OPT)) {
                        pre = handleOptCF(pre, curCF, sd, k); //OPT片段处理后返回尾节点，并使pre指向当前尾节点
                        k += (skip-1);
                    } else if(cfType.equals(Constants.CF_TYPE_ALT)) {
                        pre = handleAltCF(pre, curCF, sd, k);
                        k += (skip-1);
                    } else if(cfType.equals(Constants.CF_TYPE_LOOP)) {
                        pre = handleLoopCF(pre, curCF, sd, k);
                        k += (skip-1);
                    } else if(cfType.equals(Constants.CF_TYPE_BREAK)) {
                        pre = handleBreakCF(pre, curCF, sd, k);
                        k += (skip-1);
                    }
                    skip = 0;
                    if(k+1 < size) {
                        Message nextMessage = sd.getMessageList().get(k+1);
                        LNode gdNext = new LNode(count.getAndIncrement(), nextMessage.getSenderOrReceiverName(sd, true));
                        pre.getNext().put(gdNext, new LTransition(new LTransitionLabel(null, null, null, false)));
                        pre = gdNext;
                    }
                }
            }else {
                if(this.root == null) {
                    nextNumber = count.getAndIncrement();
                    LNode firstNode = new LNode(nextNumber, message.getSenderOrReceiverName(sd, true));
                    this.root = firstNode;
                    nextNumber = count.getAndIncrement();
                    LNode secondNode = new LNode(nextNumber, message.getSenderOrReceiverName(sd, false));
                    firstNode.getNext().put(secondNode, new LTransition(new LTransitionLabel(message.getName(), Constants.MESSAGE_TYPE, null, false)));
                    pre = secondNode;
                }else {
                    nextNumber = count.getAndIncrement();
                    LNode nextNode = new LNode(nextNumber, message.getSenderOrReceiverName(sd, false));
                    pre.getNext().put(nextNode, new LTransition(new LTransitionLabel(message.getName(), Constants.MESSAGE_TYPE, null, false)));
                    pre = nextNode;
                }
            }
        }
    }

    /**
     * 将OPT片段处理为LTS
     * @param cfStart
     * @param cf
     * @param sd
     * @param cdMessageStartPos  组合片段第一条消息的位置
     * @return
     */
    private LNode handleOptCF(LNode cfStart, CombinedFragment cf, SequenceDiagram sd, int cdMessageStartPos) {
        List<Message> messageList = sd.getMessageList();

        InteractionOperand iaOpe = cf.getOperandList().get(0); //OPT片段只有1个InteractionOperand
        List<OccurrenceSpecificationFragment> curOSF = iaOpe.getOsFragments();
        int messageNumber = curOSF.size() / 2;
        this.skip = messageNumber;
        LNode gd1 = new LNode(count.getAndIncrement(), cfStart.getLabel()), mv = gd1;
        cfStart.getNext().put(gd1, new LTransition(new LTransitionLabel(cf.getName(), Constants.CF_TYPE_OPT, "TRUE", true)));

        for(int i=cdMessageStartPos; i<messageNumber+cdMessageStartPos; i++) {
            Message curMessage = messageList.get(i);
            OccurrenceSpecificationFragment receiveOsf = curOSF.get((i-cdMessageStartPos)*2+1);
            String receiveName = sd.getLifelines().get(receiveOsf.getCoveredId()).getName();
            LNode curNode = new LNode(count.getAndIncrement(), receiveName);
            mv.getNext().put(curNode, new LTransition(new LTransitionLabel(curMessage.getName(), Constants.MESSAGE_TYPE, null, false)));
            mv = curNode;
        }
        LNode opt_cf_end = new LNode(count.getAndIncrement(), Constants.OPT_CF_END);
        mv.getNext().put(opt_cf_end, new LTransition(new LTransitionLabel(null, null, null, false)));

        LNode gd2 = new LNode(count.getAndIncrement(), cfStart.getLabel());
        cfStart.getNext().put(gd2, new LTransition(new LTransitionLabel(cf.getName(), Constants.CF_TYPE_OPT, "FALSE", true)));
        gd2.getNext().put(opt_cf_end, new LTransition(new LTransitionLabel(null, null, null, false)));

        return opt_cf_end;
    }


//    private LNode handleAltCF(LNode cfStart, CombinedFragment cf, SequenceDiagram sd, int cdMessageStartPos) {
//        List<Message> messageList = sd.getMessageList();
//        List<InteractionOperand> curIaOpe = cf.getOperandList(); // ALT片段有2个InteractionOperand
//
//        int osf1MsgNumber = curIaOpe.get(0).getOsFragments().size()/2,
//                osf2Msg2Number = curIaOpe.get(1).getOsFragments().size()/2;
//        LNode gd1 = new LNode(count.getAndIncrement(), cfStart.getLabel()), mv = gd1;
//        cfStart.getNext().put(gd1, new LTransition(new LTransitionLabel(cf.getName(), Constants.CF_TYPE_ALT, curIaOpe.get(0).getGuard().getBody(), true)));
//        Message curMessage; LNode curNode;
//        for(int i=cdMessageStartPos; i<cdMessageStartPos+osf1MsgNumber; i++) {
//            curMessage = messageList.get(i);
//            OccurrenceSpecificationFragment receiveOsf = curIaOpe.get(0).getOsFragments().get((i-cdMessageStartPos)*2+1);
//            String receiveName = sd.getLifelines().get(receiveOsf.getCoveredId()).getName();
//            curNode = new LNode(count.getAndIncrement(), receiveName);
//            mv.getNext().put(curNode, new LTransition(new LTransitionLabel(curMessage.getName(), Constants.MESSAGE_TYPE, null, false)));
//            mv = curNode;
//        }
//        LNode alt_cf_end = new LNode(count.getAndIncrement(), "ALT_CF_END");
//        mv.getNext().put(alt_cf_end, new LTransition(new LTransitionLabel(null, null, null, false)));
//
//        LNode gd2 = new LNode(count.getAndIncrement(), cfStart.getLabel()); mv = gd2;
//        cfStart.getNext().put(gd2, new LTransition(new LTransitionLabel(cf.getName(), Constants.CF_TYPE_ALT, curIaOpe.get(1).getGuard().getBody(), true)));
//        for(int i=cdMessageStartPos+osf1MsgNumber; i<cdMessageStartPos+osf1MsgNumber+osf2Msg2Number; i++) {
//            curMessage = messageList.get(i);
//            OccurrenceSpecificationFragment receiveOsf = curIaOpe.get(1).getOsFragments().get((i-cdMessageStartPos)*2+1);
//            String receiveName = sd.getLifelines().get(receiveOsf.getCoveredId()).getName();
//            curNode = new LNode(count.getAndIncrement(), receiveName);
//            mv.getNext().put(curNode, new LTransition(new LTransitionLabel(curMessage.getName(), Constants.MESSAGE_TYPE, null, false)));
//            mv = curNode;
//        }
//        mv.getNext().put(alt_cf_end, new LTransition(new LTransitionLabel(null, null, null, false)));
//        return alt_cf_end;
//    }

    /**
     * 处理Alt片段
     * @param cfStart
     * @param cf
     * @param sd
     * @param cdMessageStartPos
     * @return
     */
    private LNode handleAltCF(LNode cfStart, CombinedFragment cf, SequenceDiagram sd, int cdMessageStartPos) {
        List<Message> messageList = sd.getMessageList();
        List<InteractionOperand> curIaOpe = cf.getOperandList(); // ALT片段有2个及以上的InteractionOperand

        int curSum = cdMessageStartPos;
        LNode gdFirst, mv, curNode, alt_cf_end = null;
        Message curMessage;
        for(int i=0, iopSize = curIaOpe.size(); i<iopSize; i++) {
            int osfMsgNumber = curIaOpe.get(i).getOsFragments().size()/2;
            this.skip += osfMsgNumber;
            gdFirst = new LNode(count.getAndIncrement(), cfStart.getLabel());
            mv = gdFirst;
            cfStart.getNext().put(gdFirst, new LTransition(new LTransitionLabel(cf.getName(), Constants.CF_TYPE_ALT, curIaOpe.get(i).getGuard().getBody(), true)));
            for(int j=curSum; j<curSum+osfMsgNumber; j++) {
                curMessage = messageList.get(i);
                OccurrenceSpecificationFragment receiveOsf = curIaOpe.get(i).getOsFragments().get((j-curSum)*2+1);
                String receiveName = sd.getLifelines().get(receiveOsf.getCoveredId()).getName();
                curNode = new LNode(count.getAndIncrement(), receiveName);
                mv.getNext().put(curNode, new LTransition(new LTransitionLabel(curMessage.getName(), Constants.MESSAGE_TYPE, null, false)));
                mv = curNode;
            }
            if(alt_cf_end == null) {
                alt_cf_end = new LNode(count.getAndIncrement(), Constants.ALT_CF_END);
            }
            mv.getNext().put(alt_cf_end, new LTransition(new LTransitionLabel(null, null, null, false)));
            curSum += osfMsgNumber;
        }
        return alt_cf_end;
    }

    /**
     * 处理Loop片段
     * @param cfStart
     * @param cf
     * @param sd
     * @param cdMessageStartPos
     * @return
     */
    private LNode handleLoopCF(LNode cfStart, CombinedFragment cf, SequenceDiagram sd, int cdMessageStartPos) {
        List<Message> messageList = sd.getMessageList();

        InteractionOperand iaOpe = cf.getOperandList().get(0); //LOOP片段只有1个InteractionOperand
        List<OccurrenceSpecificationFragment> curOSF = iaOpe.getOsFragments();
        int messageNumber = curOSF.size() / 2;
        skip += messageNumber;

        String condition = iaOpe.getGuard().getBody();//loop条件符合这样的形式：“n=x;n<(>)N;n+(-)=k”
        String[] conditionPart = condition.split(",");
        int initValue = Integer.parseInt(conditionPart[0].split("=")[1]),
                maxValue = Integer.parseInt(conditionPart[1].contains("<") ? conditionPart[1].split("<")[1] : conditionPart[1].split(">")[1]);
        int step = Integer.parseInt(conditionPart[2].split("=")[1]);
        step = conditionPart[2].contains("-") ? step*-1 : step;

        LNode gd1 = new LNode(count.getAndIncrement(), cfStart.getLabel()), mv = gd1;
        cfStart.getNext().put(gd1, new LTransition(new LTransitionLabel(cf.getName(), Constants.CF_TYPE_LOOP, condition, true)));

        for(int k=initValue; k<maxValue; k+=step) {
            for(int i=cdMessageStartPos; i<cdMessageStartPos+messageNumber; i++) {
                Message curMessage = messageList.get(i);
                OccurrenceSpecificationFragment receiveOsf = curOSF.get((i-cdMessageStartPos)*2+1);
                String receiveName = sd.getLifelines().get(receiveOsf.getCoveredId()).getName();
                LNode curNode = new LNode(count.getAndIncrement(), receiveName);
                mv.getNext().put(curNode, new LTransition(new LTransitionLabel(curMessage.getName(), Constants.MESSAGE_TYPE, null, false)));
                mv = curNode;
            }
//            if(k != maxValue-1) {
//                LNode cycle = new LNode(count.getAndIncrement(), gd1.getLabel());
//                mv.getNext().put(cycle, new LTransition(new LTransitionLabel(null, Constants.CF_TYPE_LOOP, null, true)));
//                mv = cycle;
//            }
        }
        LNode loop_cf_end = new LNode(count.getAndIncrement(), Constants.LOOP_CF_END);
        mv.getNext().put(loop_cf_end, new LTransition(new LTransitionLabel(null, null, null, false)));

        LNode gd2 = new LNode(count.getAndIncrement(), cfStart.getLabel());
        cfStart.getNext().put(gd2, new LTransition(new LTransitionLabel(cf.getName(), Constants.CF_TYPE_LOOP, "NOT"+condition, true)));
        gd2.getNext().put(loop_cf_end, new LTransition(new LTransitionLabel(null, null, null, false)));
        return loop_cf_end;
    }

    /**
     * 处理break片段
     * @param cfStart
     * @param cf
     * @param sd
     * @param cdMessageStartPos
     * @return
     */
    private LNode handleBreakCF(LNode cfStart, CombinedFragment cf, SequenceDiagram sd, int cdMessageStartPos) {
        List<Message> messageList = sd.getMessageList();

        InteractionOperand iaOpe = cf.getOperandList().get(0); //BREAK片段只有1个InteractionOperand
        List<OccurrenceSpecificationFragment> curOSF = iaOpe.getOsFragments();
        int messageNumber = curOSF.size() / 2;
        LNode gd1 = new LNode(count.getAndIncrement(), cfStart.getLabel()), mv = gd1;
        cfStart.getNext().put(gd1, new LTransition(new LTransitionLabel(cf.getName(), Constants.CF_TYPE_BREAK, iaOpe.getGuard().getBody(), true)));
        for(int i=cdMessageStartPos; i<messageNumber+cdMessageStartPos; i++) {
            Message curMessage = messageList.get(i);
            OccurrenceSpecificationFragment receiveOsf = curOSF.get((i-cdMessageStartPos)*2+1);
            String receiveName = sd.getLifelines().get(receiveOsf.getCoveredId()).getName();
            LNode curNode = new LNode(count.getAndIncrement(), receiveName);
            mv.getNext().put(curNode, new LTransition(new LTransitionLabel(curMessage.getName(), Constants.MESSAGE_TYPE, null, false)));
            mv = curNode;
        }
        LNode break_cf_end = new LNode(count.getAndIncrement(), Constants.BREAK_CF_END);
        mv.getNext().put(break_cf_end, new LTransition(new LTransitionLabel(null, null, null, false)));

        LNode gd2 = new LNode(count.getAndIncrement(), cfStart.getLabel());
        mv = gd2;
        cfStart.getNext().put(gd2, new LTransition(new LTransitionLabel(cf.getName(), Constants.CF_TYPE_BREAK, "NOT"+iaOpe.getGuard().getBody(), true)));
        Message nextMsg = messageList.get(messageNumber+cdMessageStartPos);
        LNode nextMsgSender = new LNode(count.getAndIncrement(), nextMsg.getSenderOrReceiverName(sd, true));
        mv.getNext().put(nextMsgSender, new LTransition(new LTransitionLabel(null, null, null, false)));
        mv = nextMsgSender;
        for(int i=messageNumber+cdMessageStartPos; i<messageList.size(); i++) {
            Message curMsg = messageList.get(i);
            LNode curNode = new LNode(count.getAndIncrement(), curMsg.getSenderOrReceiverName(sd, false));
            mv.getNext().put(curNode, new LTransition(new LTransitionLabel(curMsg.getName(), Constants.MESSAGE_TYPE, null, false)));
            mv = curNode;
        }
        this.skip = messageList.size()-cdMessageStartPos;
        this.LTS_END = new LNode(count.getAndIncrement(), Constants.LTS_END_NODE);
        mv.getNext().put(this.LTS_END, new LTransition(new LTransitionLabel(null, null, null, false)));
        break_cf_end.getNext().put(this.LTS_END, new LTransition(new LTransitionLabel(null, null, null, false)));
        return break_cf_end;
    }

    public LTS getLTS() {
        LTS lts = new LTS();
        transform();
        lts.buildLts(this.root);
        return lts;
    }

    /**
     * 测试
     */
    public void test() {
        LTS ret = getLTS();
        List<List<LTSNodePath>> allPaths = LtsUtil.getAllPath(ret.getStart());
        LtsUtil.printAllPath(allPaths);
    }

    public static void main(String[] args) {
        // loop测试文件  "E:\tmp\xml\Test\" + "CFTestLoop.xml
        // break测试文件 "E:\tmp\xml\Test\" + "CFTestBreak.xml
        String testPath = "E:\\tmp\\xml\\Test\\" + "CFTestBreak.xml";
        TransformSd2Lts transformSd2Lts = new TransformSd2Lts(testPath);
        transformSd2Lts.test();
    }
}
