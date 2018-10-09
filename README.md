# Notifysales App
App for notification to allocation responsible when assignmentResponse is about to end.
Scheduler set to poll for assignmentResponses ending within a configurable number of weeks
and posting to a slack channel. Now set to check for ending within today and 8 weeks ahead.
A scheduled task resets sent data which will mean notification will be resent if criteria is still valid.
>Polling and post interval is also configurable in code.
>The person responsible for allocation per team has to be configured in service.
>Notified ending assignmentResponses and data not complete is stored in a SQL DB.
>Resets (is emptied) thursday at 9 am every week by default.
### Slack setup
Slack integration is done by adding the web hook URL for the Slack application 
as a ENV variable.  
>The webhook URL for the combination of channel and app is configured from the app.
>Check https://api.slack.com/apps

## build with maven
```
mvnw.cmd clean install
```
## properties
Add environment variables
```
cinode.password=${CINODE_PASSWORD} set to account to run service as needs full Cinode access.
cinode.user=${CINODE_USER} set to account to run service as needs full Cinode access.
spring.datasource.jdbc-url=${JDBC_DATABASE_URL} is set in heroku, need to be set locally
spring.datasource.username=${JDBC_DATABASE_USERNAME} is set in heroku, need to be set locally
spring.datasource.password=${JDBC_DATABASE_PASSWORD} is set in heroku, need to be set locally
slack.notification.missingdata.slackid=${MISSING_DATA_SLACK_ID} ex U12345
slack.missing.data.webhook.url=${SLACK_MISSING_DATA_WEBHOOK_URL} info about missing data channel
slack.notification.webhook.url=${SLACK_NOTIFICATION_WEBHOOK_URL} allocation notification channel
slack.notification.reset.cron=${SLACK_NOTIFICATION_RESET_CRON_SCHEDULE:0 0 9 * * THU} job that deletes data for re-send
```

## run from cmd-prompt
```
>java -jar target/notifysales-0.0.1-SNAPSHOT.jar
```

## swagger 
For showing available endpoints for communicating with service via Browser or REST client.
```
http://<host>:8080/swagger-ui.html
http://<host>:8080/v2/api-docs
```

## heroku deploy
The service is built to run on Heroku with a Heroku Postgres :: Database add-on.
Requires heroku cli and heroku account connected to service to deploy.
### re-deploys
To re-deploy the service with a new version
```
>heroku deploy:jar target/notifysales-0.0.1-SNAPSHOT.jar --app rocky-anchorage-42328
>heroku open --app rocky-anchorage-42328
>heroku logs --app rocky-anchorage-42328 --tail
```

### for new deploys
To deploy a completely new service with another name.
```
>heroku plugins:install heroku-cli-deploy
>heroku create --no-remote    ange usr/psw -> auto name
```
### stop temporary
To make the service go to sleep it can be scaled down to 0 dynos.
It is also possible to block REST access by setting the maintenance to on.
```
>heroku maintenance:on --app rocky-anchorage-42328
>heroku ps:scale web=0 --app rocky-anchorage-42328
```
