FROM python:3.10-alpine

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

ENV APP_NAME dockerdaemon

WORKDIR /usr/local/src/${APP_NAME}

COPY tciDaemonServer.py .
#COPY serial ./serial

ENTRYPOINT ["python", "tciDaemonServer.py" ]

EXPOSE 40405