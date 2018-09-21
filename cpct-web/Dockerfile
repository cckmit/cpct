FROM centosjava8:1.7 


#COPY target/bss-order-batch-inbound-dubbo-1.0-assembly.tar.gz /app/dubbo.tar.gz
COPY target/cpct-web.jar /app/cpct-web.jar
#RUN cd /app && mkdir dubbo && tar xzf dubbo.tar.gz -C dubbo --strip-components 1 && rm -rf *.tar.gz && chmod 777 -R /app  && chown -R isale:isale /app
RUN cd /app && mkdir project && mv /app/cpct-web.jar /app/project && chmod 777 -R /app  && chown -R isale:isale /app

WORKDIR /app
ENV LANG en_US.UTF-8
ENV TZ Asia/Shanghai

#VOLUME /app/dubbo/logs
USER isale 
CMD ["/app/dubbo/bin/start.sh"]
