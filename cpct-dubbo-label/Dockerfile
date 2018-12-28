FROM centosjava8:1.7 


COPY target/cpct-dubbo-label.jar /app/cpct-dubbo-label.jar
RUN cd /app && mkdir project && mv /app/cpct-dubbo-label.jar /app/project && chmod 777 -R /app  && chown -R isale:isale /app

WORKDIR /app
ENV LANG en_US.UTF-8
ENV TZ Asia/Shanghai

#VOLUME /app/dubbo/logs
USER isale 
ENTRYPOINT ["nohup","java", "-Xmx8000m","-Xms4000m","-Xmn256m","-XX:PermSize=128m","-Xss256k","-Dcom.sun.management.jmxremote","-Dcom.sun.management.jmxremote.port=12345","-Dcom.sun.management.jmxremote.ssl=false","-Dcom.sun.management.jmxremote.authenticate=false", "-jar","/app/project/cpct-dubbo-label.jar","&"]
