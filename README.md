# build-microservice-with-spring-cloud-netflixoss

![alt text](https://github.com/Hitman007IN/build-microservice-with-spring-cloud-netflixoss/blob/master/screenshot/microservice_architecture.png)

# Tech Stack
- Java 1.8
- Spring Boot 2.x
- Spring Cloud Netflix Hoxton.SR1
- Spring Cloud Security
- JWT

# Spring + Netflix OSS Stack
- Netflix Eureka Server
- Netflix Zuul
- Netflix Ribbon
- Netflix Hystrix
- Netflix Turbine
- Netflix Feign
- Sleuth
- Zipkin


# Steps to bring it up

1) Spin up Eureka server
- mvn clean install -DskipTests
- java -jar my-eureka-server-0.0.1-SNAPSHOT.jar
- Check on http://localhost:8761

2) Spin up Service A
- mvn clean install -DskipTests
- java -jar -Dserver.port=8080 my-service-a-0.0.1-SNAPSHOT.jar
- java -jar -Dserver.port=9080 my-service-a-0.0.1-SNAPSHOT.jar
- Check on http://localhost:8080/health, http://localhost:8080/fetch-bc

3) Spin up Service B
- mvn clean install -DskipTests
- java -jar -Dserver.port=8081 my-service-b-0.0.1-SNAPSHOT.jar
- java -jar -Dserver.port=9081 my-service-b-0.0.1-SNAPSHOT.jar
- Check on http://localhost:8081/health, http://localhost:8081/fetch-b

4) Spin up Service C
- mvn clean install -DskipTests
- java -jar -Dserver.port=8082 my-service-c-0.0.1-SNAPSHOT.jar
- java -jar -Dserver.port=9082 my-service-c-0.0.1-SNAPSHOT.jar
- Check on http://localhost:8082/health, http://localhost:8082/fetch-c

5) Spin up Zuul Gateway
- Only Service A is exposed to outside through Zuul
- mvn clean install -DskipTests
- java -jar my-zuul-gateway-0.0.1-SNAPSHOT.jar
- Check on http://localhost:8765/health, http://localhost:8765/fetch-bc

6) Spin up Auth Server
- Authenticate each request passing through zuul with JWT token
- mvn clean install -DskipTests
- java -jar my-auth-server-0.0.1-SNAPSHOT.jar
- Check on http://localhost:8765/health and you should get 401 Unauthorized
- To get JWT token, call 
	- HTTP POST
	- http://localhost:8765/auth
	- JSON body
	``` 
	    {
          "username":"vishakh",
          "password":"12345"
        }
	```
- Once received, call http://localhost:8765/health, http://localhost:8765/fetch-bc with header Authorization: Bearer {TOKEN}

7) Spin up Hystrix Dashboard
- mvn clean install -DskipTests
- java -jar my-hystrix-dsahboard-0.0.1-SNAPSHOT.jar
- To add some load
	- while true; do curl -i http://localhost:8080/fetch-bc; done
	- while true; do curl -i http://localhost:8081/fetch-b; done

8) Spin up Zipkin server and ui
- docker run -d -p 9411:9411 openzipkin/zipkin
- http://localhost:9411/zipkin/

9) Spin up ELK in local
- Create 02-beats-input.conf
```
input {
  tcp {
    port => 5044
    ssl => false
  }
}
```
- Create 30-output.conf
```
filter {
  json {
    source => "message"
  }
}
output {
  elasticsearch {
    hosts => ["localhost:9200"]
    manage_template => false
    index => "logstash-local"
  }
}
```
- Create Dockerfile
```
FROM sebp/elk

# overwrite existing file
RUN rm /etc/logstash/conf.d/30-output.conf
COPY 30-output.conf /etc/logstash/conf.d/30-output.conf

RUN rm /etc/logstash/conf.d/02-beats-input.conf
COPY 02-beats-input.conf /etc/logstash/conf.d/02-beats-input.conf
```
- Build docker with docker build -t local-elk .
- Run docker image with docker run -p 5601:5601 -p 9200:9200 -p 5044:5044 -it --name elk local-elk
- Kibana on port 5601, ElasticSearch on port 9200 and LogStash on port 5044
- Check on http://localhost:5601 for Kibana

