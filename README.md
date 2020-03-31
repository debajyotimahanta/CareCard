### Local dev
The entire stack should run locally without the need to setup anything. 
Things like SNS Notification, Key encrypt/decryption might not work. But we should be able to run test and mock
these up for Integration test
### Local dev with AWS
* Set the following ENV variables. You can also set these in the Intellij Run Config as env variables
```
AWS_REGION=us-west-2;
AWS_ACCESS_KEY_ID=***A5KB;
AWS_SECRET_ACCESS_KEY=***ewmv30;
DB_ENDPOINT=database-1.cluster-c7lv0nrezsdg.us-west-2.rds.amazonaws.com:3306;
MASTER_KEY_ID=arn:aws:kms:us-west-2:008731829883:key/4cfadfd2-d913-4a2b-8e66-db0144438368;
spring_profiles_active=aws
DB_USER=dblocal
```
* Either do `F5`/`Cmd+R` or `./gradlew bootRun` should run the app
* There is `git prehook` which will run a build before push. It will also checkout all the local change that was 
part of the last push. You can ignore it by doing `git reset --hard`

### AWS Stack
We are using cloud formation to setup the entire stack. All the cloud formation templates are located in the `aws` folder. 
Here are is sequence in which it needs to run.
```$xslt
# I am assuming ssh_key exists alread in this region. Without this key you wont be able to ssh into the bastions
export stackPrefix=dev
aws cloudformation --region ca-central-1 create-stack --stack-name $stackPrefix-1-network-v1 --template-body file://network.yaml --parameters ParameterKey=KeyName,ParameterValue=deba

aws cloudformation --region ca-central-1 create-stack --stack-name $stackPrefix-2-infra-v1 --template-body file://infra.yaml

aws cloudformation --region ca-central-1 create-stack --stack-name $stackPrefix-3-db-v1 --template-body file://db.yaml --parameters ParameterKey=NetworkStackName,ParameterValue=$stackPrefix-1-network-v1

aws cloudformation --region ca-central-1 create-stack --stack-name $stackPrefix-4-iam-v1 --template-body file://iam.yaml --parameters ParameterKey=InfraStackName,ParameterValue=$stackPrefix-2-infra-v1 ParameterKey=DbStackName,ParameterValue=$stackPrefix-3-db-v1 --capabilities CAPABILITY_NAMED_IAM

aws cloudformation --region ca-central-1 create-stack --stack-name $stackPrefix-5-secrets-v1 --template-body file://secerts.yaml --parameters ParameterKey=IamStackName,ParameterValue=$stackPrefix-4-iam-v1

aws cloudformation --region ca-central-1 create-stack --stack-name $stackPrefix-6-ebs-v1 --template-body file://ebs.yaml --parameters ParameterKey=NetworkStackName,ParameterValue=$stackPrefix-1-network-v1 ParameterKey=InfraStackName,ParameterValue=$stackPrefix-2-infra-v1  ParameterKey=DbStackName,ParameterValue=$stackPrefix-3-db-v1 ParameterKey=IamStackName,ParameterValue=$stackPrefix-4-iam-v1 ParameterKey=SecretsStackName,ParameterValue=$stackPrefix-X-secrets-v1 ParameterKey=KeyName,ParameterValue=deba
 
 
```

`NOTE:` The sequence matters as the output of one stack is used in another