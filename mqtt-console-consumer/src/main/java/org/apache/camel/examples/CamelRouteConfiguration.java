/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.examples;


import javax.net.ssl.SSLContext;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.paho.PahoComponent;
import org.apache.camel.spi.ComponentCustomizer;
import org.apache.camel.util.jsse.SSLContextParameters;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CamelRouteConfiguration extends RouteBuilder {

  private static final Logger log = LoggerFactory.getLogger(CamelRouteConfiguration.class);

  @Bean
  @Autowired
  MqttConnectOptions mqttConnectOptions(@Value("${camel.component.paho.user-name}") String username, 
                                        @Value("${camel.component.paho.password}") String password,
                                        CamelContext camelContext) throws Exception {
    MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
    
    mqttConnectOptions.setUserName(username);
    mqttConnectOptions.setPassword(password.toCharArray());
    
    mqttConnectOptions.setCleanSession(true);
    //mqttConnectOptions.setWill("will", "I got disconnected!".getBytes(StandardCharsets.UTF_8), 2, false);
    
    SSLContextParameters sslContextParameters = camelContext.getSSLContextParameters();
    if (sslContextParameters != null) {
      SSLContext sslContext = sslContextParameters.createSSLContext(camelContext);
      mqttConnectOptions.setSocketFactory(sslContext.getSocketFactory());
    }
    
    return mqttConnectOptions;
  }
  
  @Bean
  @Autowired
  ComponentCustomizer<PahoComponent> pahoComponentCustomizer(MqttConnectOptions mqttConnectOptions) {
    return (component) -> {
      component.setConnectOptions(mqttConnectOptions);
    };
  }

  @Override
  public void configure() {

    from("paho://{{mqtt.destination.name}}?autoReconnect=true")
      .setHeader("MqttMessageSize").exchange((exchange) -> {
        return exchange.getIn().getBody(MqttMessage.class).getPayload().length;
      })
      .choice()
        .when(simple("${header.MqttMessageSize} >= ${properties:mqtt.min-large-message-size:1024}"))
          .to("file:{{file.directory:target/output/}}")
        .otherwise()
          .convertBodyTo(String.class).to("stream:out")
      .endChoice()
    ;
  }
}
