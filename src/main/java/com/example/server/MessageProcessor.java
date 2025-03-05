package com.example.server;

import com.example.commands.CmdIsMaster;
import com.example.commands.Command;
import com.example.protocol.MongoMessage;
import com.example.transport.MongoServerHandler;
import io.netty.channel.ChannelHandlerContext;
import org.bson.BsonBinaryWriter;
import org.bson.RawBsonDocument;
import org.bson.io.BasicOutputBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;

import static com.example.transport.TransportConstants.*;

public class MessageProcessor {
    private static final Logger log = LoggerFactory.getLogger(MessageProcessor.class);

    private final HashMap<String, Command> cmdMap;

    MessageProcessor() {
        cmdMap = new HashMap<String, Command>();
        CmdIsMaster cmdIsMaster = new CmdIsMaster();
        cmdIsMaster.register(cmdMap);
    }

    public RawBsonDocument handleMessage(ChannelHandlerContext opCtx, MongoMessage msg) {
        MongoMessage rsp = null;
        log.info("get cmd name {}", msg.getCommandName());
        Command cmd = cmdMap.get(msg.getCommandName());
        if (cmd == null) {
            throw new UnsupportedOperationException(String.format("%s is not a legal command", msg.getCommandName()));
        }
        return cmd.run(opCtx, msg);
    }
}
