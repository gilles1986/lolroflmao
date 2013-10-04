/*
 * Copyright 2002-2013 the original author or authors.
 *
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
 */

package org.lolroflmao.boot.samples.websocket.config;

import org.lolroflmao.boot.SpringApplication;
import org.lolroflmao.boot.autoconfigure.EnableAutoConfiguration;
import org.lolroflmao.boot.samples.websocket.client.GreetingService;
import org.lolroflmao.boot.samples.websocket.client.SimpleGreetingService;
import org.lolroflmao.boot.samples.websocket.echo.DefaultEchoService;
import org.lolroflmao.boot.samples.websocket.echo.EchoService;
import org.lolroflmao.boot.samples.websocket.echo.EchoWebSocketHandler;
import org.lolroflmao.boot.samples.websocket.snake.SnakeWebSocketHandler;
import org.lolroflmao.boot.web.SpringBootServletInitializer;
import org.lolroflmao.context.annotation.Bean;
import org.lolroflmao.context.annotation.Configuration;
import org.lolroflmao.web.socket.WebSocketHandler;
import org.lolroflmao.web.socket.support.PerConnectionWebSocketHandler;

@Configuration
@EnableAutoConfiguration
public class SampleWebSocketsApplication extends SpringBootServletInitializer {

	@Override
	protected Class<?> getConfigClass() {
		return SampleWebSocketsApplication.class;
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleWebSocketsApplication.class, args);
	}

	@Bean
	public EchoService echoService() {
		return new DefaultEchoService("Did you say \"%s\"?");
	}

	@Bean
	public GreetingService greetingService() {
		return new SimpleGreetingService();
	}

	@Bean(name = "/echo")
	public WebSocketHandler echoWebSocketHandler() {
		return new EchoWebSocketHandler(echoService());
	}

	@Bean(name = "/snake")
	public WebSocketHandler snakeWebSocketHandler() {
		return new PerConnectionWebSocketHandler(SnakeWebSocketHandler.class);
	}

}
