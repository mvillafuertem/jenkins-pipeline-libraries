import io.github.mvillafuertem.aws.AwsWorkflow

void call(Map parameters = [:]) {
    AwsWorkflow awsWorkflow = new AwsWorkflow(this)
    awsWorkflow.call()
}

return this