FROM centosjava8:1.7 


COPY target/cpct-dubbo-label.jar /app/cpct-dubbo-label.jar
COPY target/classes/dubbo/unregister.sh /app/unregister.sh
RUN cd /app && mkdir project && mv /app/cpct-dubbo-label.jar /app/project && mv /app/unregister.sh /app/project && chmod 777 -R /app  && chown -R isale:isale /app

WORKDIR /app
ENV LANG en_US.UTF-8
ENV TZ Asia/Shanghai

#VOLUME /app/dubbo/logs
USER isale 
ENTRYPOINT ["nohup","java","-Xmx4000m","-Xms4000m","-Xmn256m","-XX:PermSize=128m","-Xss256k", "-jar","/app/project/cpct-dubbo-label.jar","&"]
