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

package lolroflmao.boot.websocket.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lolroflmao.boot.websocket.client.GreetingService;
import lolroflmao.boot.websocket.client.SimpleGreetingService;
import lolroflmao.boot.websocket.echo.DefaultEchoService;
import lolroflmao.boot.websocket.echo.EchoService;
import lolroflmao.boot.websocket.echo.EchoWebSocketHandler;
import lolroflmao.boot.websocket.hero.HeroWebSocketHandler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.support.PerConnectionWebSocketHandler;

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

	@Bean(name = "/hero")
	public WebSocketHandler heroWebSocketHandler() {
		return new PerConnectionWebSocketHandler(HeroWebSocketHandler.class);
	}

}
