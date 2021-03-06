# Notifysales App
App for notification to allocation responsible when assignment is about to end.
Scheduler set to poll for assignment ending within a configurable number of weeks
and posting to a slack channel. Now set to check for ending within today and 8 weeks ahead.
A scheduled task resets sent data which will mean notification will be resent if criteria is still valid.
>Polling interval from Cinode is configurable and scheduled by CRON job.
>Default run at 7.30 AM every day.
>The person responsible for allocation per team has to be configured in service.
>Notified ending assignment and data not complete is stored in a SQL DB.
>Resets (is emptied) thursday at 8 AM every week by default.
### Slack setup
Slack integration is done by adding the web hook URL for the Slack application 
as a ENV variable.  
>The webhook URL for the combination of channel and app is configured from the app.
>Check https://api.slack.com/apps

## Build with maven
```
mvnw.cmd clean install
```

## Release with maven
Skapar en release version och steppar pom till nästa snapshot
Kolla att allt är ok först med dry run
```
mvn release:prepare -DdryRun=true  

mvn release:prepare

mvn release:perform
```

## Properties
Add environment variables
```
cinode.password=${CINODE_PASSWORD} set to account to run service as needs full Cinode access.
cinode.user=${CINODE_USER} set to account to run service as needs full Cinode access.
cinode.poll.cron=${CINODE_POLL_CRON_SCHEDULE:0 0 8 * * *} schedule for job to fetch all projects in batches
spring.datasource.jdbc-url=${JDBC_DATABASE_URL} is set in heroku, need to be set locally
spring.datasource.username=${JDBC_DATABASE_USERNAME} is set in heroku, need to be set locally
spring.datasource.password=${JDBC_DATABASE_PASSWORD} is set in heroku, need to be set locally
slack.notification.missingdata.slackid=${MISSING_DATA_SLACK_ID} ex U12345
slack.missing.data.webhook.url=${SLACK_MISSING_DATA_WEBHOOK_URL} info about missing data channel
slack.notification.webhook.url=${SLACK_NOTIFICATION_WEBHOOK_URL} allocation notification channel
slack.notification.reset.cron=${SLACK_NOTIFICATION_RESET_CRON_SCHEDULE:0 30 7 * * THU}  schedule for job that deletes data for re-send
```

## Run from cmd-prompt
```
java -jar target/notifysales-<version>.jar
```

## Swagger 
For showing available endpoints for communicating with service via Browser or REST client.
```
http://<host>:8080/swagger-ui.html
http://<host>:8080/v2/api-docs
```

## Heroku deploy
The service is built to run on Heroku with a Heroku Postgres :: Database add-on.
Requires heroku cli and heroku account connected to service to deploy.
### For re-deploys
To re-deploy the service with a new version
```
>heroku deploy:jar target/notifysales-<version>.jar --app <app-name>
>heroku open --app <app-name>
>heroku logs --app <app-name> --tail
```

### For new deploys
To deploy a completely new service with another name.
```
>heroku plugins:install heroku-cli-deploy
>heroku create --no-remote    ange usr/psw -> auto name
```
### Stop temporary
To make the service go to sleep it can be scaled down to 0 dynos.
It is also possible to block REST access by setting the maintenance to on.
```
>heroku maintenance:on --app <app-name>
>heroku ps:scale web=0 --app <app-name>
```

## Setup local postgres
>Install postgres SQL with pgadmin LTS.
>Add local server using pgadmin LTS
>localhost / 5432

start psql terminal and create DB
```
CREATE DATABASE notificationdb;
CREATE USER notification WITH PASSWORD 'dummy';
GRANT ALL PRIVILEGES ON DATABASE "notificationdb" to notification;
```
Add connection data to ENV variables fro JDBC DB. See above.


