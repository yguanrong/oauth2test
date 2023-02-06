#!/bin/bash

if [ -n "${SERVICE_NAME}" ]; then
    echo -e "\033[32m[`date +"%Y-%m-%d %H:%M:%S"`] INFO: AppId - ${SERVICE_NAME} \033[0m"
    DB_NAME=intellif

    # logs
    if [ ! -d "/opt/logs" ]; then
        mkdir -p /opt/logs
    else
        rm -rf /opt/${SERVICE_NAME}/logs
    fi
    ln -s /opt/logs /opt/${SERVICE_NAME}/logs

    # config
    if [ ! -d "/opt/conf" ]; then
        mkdir -p /opt/conf
    fi
    ls -alh /opt
    ls -alh /opt/conf
    ls -alh /opt/${SERVICE_NAME}
    # apollo ip set
    if [ ! -f "/opt/conf/apollo-env.properties" ]; then
        mv /opt/${SERVICE_NAME}/apollo-env.properties /opt/conf
        if [ -n "$APOLLO_IP" ]; then
            sed -i "s#^dev.meta=.*#dev.meta=http://${APOLLO_IP}#g" /opt/conf/apollo-env.properties
            sed -i "s#^fat.meta=.*#fat.meta=http://${APOLLO_IP}#g" /opt/conf/apollo-env.properties
            sed -i "s#^uat.meta=.*#uat.meta=http://${APOLLO_IP}#g" /opt/conf/apollo-env.properties
            sed -i "s#^pro.meta=.*#pro.meta=http://${APOLLO_IP}#g" /opt/conf/apollo-env.properties
        fi
    else
        rm -rf /opt/${SERVICE_NAME}/apollo-env.properties
    fi
    ln -s /opt/conf/apollo-env.properties /opt/${SERVICE_NAME}/apollo-env.properties
    # apollo env name
    if [ ! -f "/opt/conf/server.properties" ]; then
        mv /opt/settings/server.properties /opt/conf
        if [ -n "$ENV" ]; then
            sed -i "s#^env=.*#env=${ENV}#g" /opt/conf/server.properties
        fi
    else
        rm -rf /opt/settings/server.properties
    fi
    ln -s /opt/conf/server.properties /opt/settings/server.properties

    echo 'ls /opt'
    ls /opt -lh

    echo 'ls /opt/conf'
    ls /opt/conf -lh

    echo 'cat /opt/build.data'
    cat /opt/${SERVICE_NAME}/build.data

    # build & version info
    count=`ls /opt/conf/*.data|wc -l`
    if [ ${count} -ne 0 ]; then rm -rf /opt/conf/*.data; fi
    cp /opt/${SERVICE_NAME}/*.data /opt/conf

    echo 'ls /opt/conf'
    ls /opt/conf -lh

#    sed -i s/intellif_base/${DB_NAME}/g /opt/${SERVICE_NAME}/flyway.conf
#    sed -i s/root/${DB_USER}/g /opt/${SERVICE_NAME}/flyway.conf

#    if [[ -n "$DB_IP" ]]; then
#        if [[ "$DB_IP" == *:* ]]; then
#            DB_PORT=${DB_IP##*:}
#            DB_IP=${DB_IP%%:*}
#        fi
#        sed -i s/127.0.0.1/${DB_IP}/g /opt/${SERVICE_NAME}/flyway.conf
#    fi

    # start app
    echo 'start app'
    cd /opt/"${SERVICE_NAME}"
    if [ -n "${TYPE}" ]; then
        if [ -z "${APOLLO_ADMIN}" ]; then echo -e "\033[31m[`date +"%Y-%m-%d %H:%M:%S"`] ERROR: Missing params APOLLO_ADMIN!!! \033[0m";exit 1; fi
        until curl --silent --fail ${APOLLO_ADMIN}; do echo -e "\033[33m[`date +"%Y-%m-%d %H:%M:%S"`] WARNING: Waiting apollo admin start up!!! \033[0m"; sleep 3; done
        if [ -n "${DELAY}" ]; then
            echo -e "\032[31m[`date +"%Y-%m-%d %H:%M:%S"`] INFO: ${SERVICE_NAME} delay ${DELAY}s startup!!! \033[0m"
            sleep ${DELAY}
        fi
        ./start.sh
    else
      echo "Type ${TYPE} ? else"
      cat apollo-env.properties
      ls -alh
        ./start.sh
    fi

else
    echo -e "\033[31m[`date +"%Y-%m-%d %H:%M:%S"`] ERROR: please set SERVICE_NAME!!! \033[0m"
    exit 1
fi