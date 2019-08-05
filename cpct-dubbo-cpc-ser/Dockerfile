FROM centosjava8:1.7 


COPY target/cpct-dubbo-cpc-ser.jar /app/cpct-dubbo-cpc-ser.jar
COPY target/classes/dubbo/unregister.sh /app/unregister.sh
RUN cd /app && mkdir project && mkdir logs && mv /app/cpct-dubbo-cpc-ser.jar /app/project && mv /app/unregister.sh /app/project && chmod 777 -R /app  && chown -R isale:isale /app

WORKDIR /app
ENV LANG en_US.UTF-8
ENV TZ Asia/Shanghai

#VOLUME /app/dubbo/logs
USER isale 
ENTRYPOINT ["nohup","java","-server", "-Xmx4000m","-Xms4000m","-Xmn2000m","-XX:PermSize=256m","-Xss256k", "-XX:+DisableExplicitGC", "-XX:+UseConcMarkSweepGC", "-XX:+CMSParallelRemarkEnabled", "-XX:+UseCMSCompactAtFullCollection", "-XX:LargePageSizeInBytes=128m", "-XX:+UseFastAccessorMethods", "-XX:+UseCMSInitiatingOccupancyOnly", "-XX:CMSInitiatingOccupancyFraction=70","-Dcom.sun.management.jmxremote","-Dcom.sun.management.jmxremote.port=12345","-Dcom.sun.management.jmxremote.ssl=false","-Dcom.sun.management.jmxremote.authenticate=false", "-jar","/app/project/cpct-dubbo-cpc.jar","&"]

