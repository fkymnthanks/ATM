package com.graduate.zl.sd2Lts.model.SeqDiagram;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 消息
 */
public class Message {

    @Getter @Setter
    private String id;

    @Getter @Setter
    private String name;

    @Getter @Setter
    private String sendEvent;

    @Getter @Setter
    private String receiveEvent;


    public Message(String id, String name, String sendEvent, String receiveEvent) {
        this.id = id;
        this.name = name;
        this.sendEvent = sendEvent;
        this.receiveEvent = receiveEvent;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", sendEvent='" + sendEvent + '\'' +
                ", receiveEvent='" + receiveEvent + '\'' +
                '}';
    }

    /**
     * 获取消息发送端或接收端名称
     * @param sd
     * @param sendSide 如果为true，则表示查询sendEvent的name
     * @return
     */
    public String getSenderOrReceiverName(SequenceDiagram sd, boolean sendSide) {
        Map<String,  OccurrenceSpecificationFragment> osFragments = sd.getOsFragments();
        OccurrenceSpecificationFragment target = null;
        if(sendSide)
            target = osFragments.get(this.getSendEvent());
        else
            target = osFragments.get(this.getReceiveEvent());
        String covered = target.getCoveredId();
        Map<String, Lifeline> lifelines = sd.getLifelines();
        return lifelines.get(covered).getName();
    }

    /**
     * 根据当前message的eventId（sendEvent或者receiveEvent）查询当前message所属的CF以及operand
     * ret[0]和ret[1]分别保存CF和InteractionOperand的Id
     * @param sd
     * @return
     */
    public String[] getBelongedCF(SequenceDiagram sd) {
        String[] ret = new String[2];
        String sendEvent = this.getSendEvent();
        if(sd.getOsFragments().get(sendEvent).isBelongToCF()) {
            ret[1] = sd.getOsFragments().get(sendEvent).getBelongInteractionOperandId();
            ret[0] = sd.getInteractionOperands().get(ret[1]).getBelongCombinedFragmentId();
        }
        return ret;
    }
}
