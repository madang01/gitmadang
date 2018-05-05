/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.example.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * Echoes back any received data from a client.
 */
public final class EchoServer {
    public static void main(String[] args) throws Exception {
    	NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boosGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
         
        // ===========================================================
        // 1. define a separate thread pool to execute handlers with
        // slow business logic. e.g database operation
        // ===========================================================
        final EventExecutorGroup group = new DefaultEventExecutorGroup(1500); //thread pool of 500
         
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            // pipeline.addLast(new LoggingHandler(LogLevel.INFO));
            
            pipeline.addLast(new StringEncoder()); 
            pipeline.addLast(new StringDecoder()); 
             
            //===========================================================
            // 2. run handler with slow business logic 
            //    in separate thread from I/O thread
            //===========================================================
            pipeline.addLast(group,"serverHandler",new ServerHandler()); 
          }
        });
         
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.bind("172.30.1.16", 19000).sync();
    }
}
