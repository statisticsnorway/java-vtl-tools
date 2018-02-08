package no.ssb.vtl.tools.rest;

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

import io.termd.core.http.HttpTtyConnection;
import io.termd.core.tty.TtyConnection;
import no.ssb.vtl.tools.termd.TtyConsole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TermdSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ThreadPoolTaskExecutor ptsExecutors;

    private HttpTtyConnection connection;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Consumer<TtyConnection> handler = new TtyConsole();
        connection = new HttpTtyConnection() {
            @Override
            protected void write(byte[] buffer) {
                try {
                    session.sendMessage(
                            new TextMessage(buffer)
                    );
                } catch (IOException e) {
                    close();
                }
            }

            @Override
            public void close() {
                try {
                    session.close();
                } catch (IOException e) {
                    // Ignore
                }
            }

            @Override
            public void execute(Runnable task) {
                ptsExecutors.execute(task);
            }

            @Override
            public void schedule(Runnable task, long delay, TimeUnit unit) {
                ptsExecutors.execute(task, unit.toMillis(delay));
            }
        };
        handler.accept(connection);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        connection.writeToDecoder(message.getPayload());
    }
}
