FROM danny02/graalvm

ENV GRAAL_DIR /graalvm-ce-1.0.0-rc6
ENV APP_NPM $GRAAL_DIR/bin/npm
ENV APP_NODE $GRAAL_DIR/bin/node

RUN apt-get update && apt-get install -yy unzip

COPY sails-hook-ignite.jar $GRAAL_DIR/jre/lib/ext/sails-hook-ignite.jar

COPY example-app.zip /tmp/example-app.zip

RUN unzip /tmp/example-app.zip -d /app

WORKDIR /app

RUN $APP_NPM install

ENTRYPOINT $APP_NODE --jvm --polyglot app.js

EXPOSE 1337