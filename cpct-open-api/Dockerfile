FROM centosjava8:1.7 


COPY target/cpct-open-api.jar /app/cpct-open-api.jar
COPY target/classes/dubbo/unregister.sh /app/unregister.sh
RUN cd /app && mkdir project && mkdir logs && mv /app/cpct-open-api.jar /app/project && mv /app/unregister.sh /app/project && chmod 777 -R /app  && chown -R isale:isale /app

WORKDIR /app
ENV LANG en_US.UTF-8
ENV TZ Asia/Shanghai

#VOLUME /app/dubbo/logs
USER isale 
ENTRYPOINT ["nohup","java", "-jar","/app/project/cpct-open-api.jar","&"]
