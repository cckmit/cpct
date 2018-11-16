FROM centosjava8:1.7 


COPY target/cpct-dubbo-label.jar /app/cpct-dubbo-label.jar
RUN cd /app && mkdir project && mv /app/cpct-dubbo-label.jar /app/project && chmod 777 -R /app  && chown -R isale:isale /app

WORKDIR /app
ENV LANG en_US.UTF-8
ENV TZ Asia/Shanghai

#VOLUME /app/dubbo/logs
USER isale 
ENTRYPOINT ["nohup","java", "-jar","/app/project/cpct-dubbo-label.jar","--spring.profiles.active=prod","&"]
