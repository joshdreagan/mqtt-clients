# MQTT Clients

## Producer

__Plain text__

```
mvn spring-boot:run '-Dmqtt.destination.name=foo' '-Dcamel.component.paho.broker-url=tcp://localhost:1883' '-Dcamel.component.paho.user-name=alice' '-Dcamel.component.paho.password=bosco'
```

__SSL__

```
mvn spring-boot:run '-Dmqtt.destination.name=foo' '-Dcamel.component.paho.broker-url=ssl://localhost:8883' '-Dcamel.component.paho.user-name=alice' '-Dcamel.component.paho.password=bosco' '-Dcamel.ssl.config.trust-managers.key-store.resource=../client.ts' '-Dcamel.ssl.config.trust-managers.key-store.password=abcd1234'
```

## Consumer

__Plain text__

```
mvn spring-boot:run '-Dmqtt.destination.name=foo' '-Dcamel.component.paho.broker-url=tcp://localhost:1883' '-Dcamel.component.paho.user-name=bob' '-Dcamel.component.paho.password=bosco'
```

__SSL__

```
mvn spring-boot:run '-Dmqtt.destination.name=foo' '-Dcamel.component.paho.broker-url=ssl://localhost:8883' '-Dcamel.component.paho.user-name=bob' '-Dcamel.component.paho.password=bosco' '-Dcamel.ssl.config.trust-managers.key-store.resource=../client.ts' '-Dcamel.ssl.config.trust-managers.key-store.password=abcd1234'
```
