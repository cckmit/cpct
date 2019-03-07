FROM centosjava8:1.7 


COPY target/cpct-dubbo-server.jar /app/cpct-dubbo-server.jar
COPY target/classes/dubbo/unregister.sh /app/unregister.sh
RUN cd /app && mkdir project && mkdir logs && mv /app/cpct-dubbo-server.jar /app/project && mv /app/unregister.sh /app/project && chmod 777 -R /app  && chown -R isale:isale /app

WORKDIR /app
ENV LANG en_US.UTF-8
ENV TZ Asia/Shanghai

#VOLUME /app/dubbo/logs
USER isale 
ENTRYPOINT ["nohup","java", "-Xmx7500m","-Xms7500m","-Xmn5000m","-XX:PermSize=256m","-Xss256k","-Dcom.sun.management.jmxremote","-Dcom.sun.management.jmxremote.port=12345","-Dcom.sun.management.jmxremote.ssl=false","-Dcom.sun.management.jmxremote.authenticate=false", "-jar","/app/project/cpct-dubbo-server.jar","&"]

