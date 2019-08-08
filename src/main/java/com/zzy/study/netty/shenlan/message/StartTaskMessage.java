package com.zzy.study.netty.shenlan.message;

import com.zzy.study.netty.shenlan.util.ByteUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.commons.lang3.StringUtils;

public class StartTaskMessage {
	
	/**
	 * 开始任务
	 * @param receiverID
	 * @param taskID
	 * @param startTime
	 * @return
	 */
	public static NettyMessage Builder(int receiverID, int taskID, String startTime) {
		TaskBody body = new TaskBody(taskID, startTime);
		Header header = new Header();
		header.command = MessageType.TASK_START.value();
        header.receiverID = receiverID;
        header.dataLength = (short) body.length();
        return new NettyMessage(header, body);
    }

    public static class TaskBody extends Body {
    	
    	public static final int StartTimeSize = 20;
    	
        public int taskID;//任务id
        public String startTime;
        
        public TaskBody(int taskID, String startTime) {
        	this.taskID = taskID;
        	setStartTime(startTime);
        }
        
        public void setStartTime(String startTime) {
        	this.startTime = StringUtils.rightPad(startTime, StartTimeSize, NettyMessage.COVER);
        }

        @Override
        public int length() {
        	return NettyMessage.Uint32Length + StartTimeSize;
        }

        @Override
        public byte[] toByteArray()  {
            ByteBuf out = Unpooled.buffer(length());
            out.writeInt(taskID);
            out.writeBytes(startTime.getBytes());
            return out.array();
        }

        @Override
        public void encode(ByteBuf out) {
            out.writeInt(taskID);
            out.writeBytes(startTime.getBytes());
        }

        public static TaskBody fromByteBuf(ByteBuf byteBuf){
            try {
                byte[] startTimeBytes=new byte[StartTimeSize];
                int taskID=byteBuf.readInt();
                byteBuf.readBytes(startTimeBytes);

                return new TaskBody(taskID,
                        ByteUtils.byteArray2String(startTimeBytes, NettyMessage.COVER));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
