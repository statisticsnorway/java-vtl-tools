package no.ssb.vtl.tools.sandbox;

/*-
 * ========================LICENSE_START=================================
 * Java VTL
 * %%
 * Copyright (C) 2016 - 2017 Hadrien Kohl
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */

import io.termd.core.http.netty.NettyWebsocketTtyBootstrap;
import io.termd.core.telnet.netty.NettyTelnetTtyBootstrap;
import no.ssb.vtl.tools.termd.TtyConsole;

import java.util.concurrent.TimeUnit;

/**
 * A console application
 */
public class Application {

    public synchronized static void main(String[] args) throws Exception {
        NettyTelnetTtyBootstrap bootstrap = new NettyTelnetTtyBootstrap().
                setHost("localhost").
                setPort(4000);
        bootstrap.start(new TtyConsole()).get(10, TimeUnit.SECONDS);
        System.out.println("Telnet server started on localhost:4000");

        NettyWebsocketTtyBootstrap bootstrapWs = new NettyWebsocketTtyBootstrap().setHost("localhost").setPort(8080);
        bootstrapWs.start(new TtyConsole()).get(10, TimeUnit.SECONDS);
        System.out.println("Web server started on localhost:8080");
        Application.class.wait();

        Application.class.wait();


    }

}
