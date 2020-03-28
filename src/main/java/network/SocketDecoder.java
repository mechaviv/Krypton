/*
 * This file is part of OrionAlpha, a MapleStory Emulator Project.
 * Copyright (C) 2018 Eric Smith <notericsoft@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package network;

import common.OrionConfig;
import game.user.ClientSocket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;
import network.packet.InPacket;
import network.security.IGCipher;
import network.security.SocketKey;
import util.Logger;
import util.Pointer;

/**
 * The server-end networking decoder. 
 * Receives incoming socket buffer lists from the remote socket.
 * 
 * @author Eric
 */
public class SocketDecoder extends ReplayingDecoder<Void> {
    private final SocketKey key;

    public SocketDecoder(SocketKey key) {
        this.key = key;
    }

    class RecvData {
        /* The maximum incoming data buffer sizes */
        class MaxSize {
            static final int
                    Client = 0x10000,
                    Center = 0x8000
            ;
        }
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Pointer<Integer> lastState = new Pointer<>(0);
        InPacket packet = new InPacket();
        ByteBuf buff;
        int state;
        
        buff = in.readBytes(4);
        try {
            state = packet.appendBuffer(buff, lastState);
            if (state > 0 && lastState.get() <= 0) {
                if (packet.decodeSeqBase(key.getSeqRcv()) != OrionConfig.CLIENT_VER) {
                    Logger.logError("Incorrect packet header sequencing [%d] | [%d]", key.getSeqRcv(), packet.decodeSeqBase(key.getSeqRcv()));
                    ctx.disconnect();
                    return;
                }
                if (packet.getDataLen() > RecvData.MaxSize.Client) {
                    Logger.logError("Received packet length overflow");
                    ctx.disconnect();
                    return;
                }
            }
        } finally {
            buff.release();
        }
        buff = in.readBytes(packet.getDataLen());
        try {
            state = packet.appendBuffer(buff, lastState);
            if (state == 2) {
                if (!packet.decryptData(key.getSeqRcv())) {
                    Logger.logError("DecryptData Failed");
                    key.updateRecv();
                    return;
                }
                key.updateRecv();

                out.add(packet);
            }
        } finally {
            buff.release();
        }
    }
}
