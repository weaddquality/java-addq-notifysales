# Notifysales App
>App for notfication to allocation responsible when assignment is about to end.
>Scheduler set to check for new notifications every 10 seconds.

### Slack setup
>Slack integration is done by adding the web hook URL for the Slack application Cinode-notisar
as a ENV variable.
>The notifications will be sent to all channels where the app is installed.
>https://api.slack.com/apps/ACBKNGLM8

## build and run
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
slack.webhook.url=${SLACK_WEBHOOK_URL} set to channel where notifications is going to be posted
slack.notification.missingdata.slackid=${MISSING_DATA_SLACK_ID} Slack userid i.e. U12345
```

## build and run
```
java -jar target/notifysales-0.0.1-SNAPSHOT.jar
```

## swagger
```
http://<host>:8080/swagger-ui.html
http://<host>:8080/v2/api-docs
```

## heroku deploy
Requires heroku cli and heroku account connected to team.
### re-deploys
```
>heroku deploy:jar target/notifysales-0.0.1-SNAPSHOT.jar --app rocky-anchorage-42328
>heroku open --app rocky-anchorage-42328
>heroku logs --app rocky-anchorage-42328 --tail
```

### for new deploys
```
>heroku plugins:install heroku-cli-deploy
>heroku create --no-remote    ange usr/psw -> auto name
```
### stop temporary
```
>heroku maintenance:on --app rocky-anchorage-42328
>heroku ps:scale web=0 --app rocky-anchorage-42328
```
