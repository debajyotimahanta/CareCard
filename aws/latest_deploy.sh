export stackPrefix=beta
export region=us-west-1
export ec2Key=beta-care-card

aws cloudformation --region $region update-stack --stack-name $stackPrefix-7-sqs-v1 --template-body file://notification.yaml --parameters ParameterKey=IamStackName,ParameterValue=$stackPrefix-4-iam-v1


aws cloudformation --region $region create-stack --stack-name $stackPrefix-6-ebs-v1 --template-body file://ebs.yaml --parameters ParameterKey=NetworkStackName,ParameterValue=$stackPrefix-1-network-v1 ParameterKey=InfraStackName,ParameterValue=$stackPrefix-2-infra-v1  ParameterKey=DbStackName,ParameterValue=$stackPrefix-3-db-v1 ParameterKey=IamStackName,ParameterValue=$stackPrefix-4-iam-v1 ParameterKey=SecretsStackName,ParameterValue=$stackPrefix-5-secrets-v1 ParameterKey=KeyName,ParameterValue=$ec2Key ParameterKey=ApiUrl,ParameterValue=https://api.beta.coronacarecard.com ParameterKey=FrontEndUrl,ParameterValue=https://www.beta.ccarecard.com