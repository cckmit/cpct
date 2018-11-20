FROM centosjava8:1.7 


COPY target/cpct-web.jar /app/cpct-web.jar
RUN cd /app && mkdir project && mkdir logs && mv /app/cpct-web.jar /app/project && chmod 777 -R /app  && chown -R isale:isale /app

WORKDIR /app
ENV LANG en_US.UTF-8
ENV TZ Asia/Shanghai

#VOLUME /app/dubbo/logs
USER isale 
ENTRYPOINT ["nohup","java", "-jar","/app/project/cpct-web.jar","--spring.profiles.active=pst","&"]
