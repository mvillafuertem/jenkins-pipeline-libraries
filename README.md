<p align="center"><img width="200" src="https://raw.githubusercontent.com/mvillafuertem/jenkins-shared-libraries-scala/master/jenkins-icon.svg"/></p>
<h1 align="center">Jenkins Shared Libraries Scala</h1>
<p align="center">
  <a href="https://github.com/scala/scala/releases">
    <img src="https://img.shields.io/badge/scala-2.13.3-red.svg?logo=scala&logoColor=red"/>
  </a>  
  <a href="https://www.oracle.com/technetwork/java/javase/11all-relnotes-5013287.html">
    <img src="https://img.shields.io/badge/jdk-11.0.8-orange.svg?logo=java&logoColor=white"/>
  </a>
  <a href="https://github.com/mvillafuertem/scala/actions?query=workflow%3A%22scalaci%22">
    <img src="https://github.com/mvillafuertem/scala/workflows/scalaci/badge.svg"/>
  </a>
</p> 

****

The Project "jenkins-shared-libraries-scala" offers unit test pipelines with scala test.

****

## Simple Pipeline

```groovy
library 'jenkins-shared-libraries-scala'

pipeline {
    agent any

    stages {
        stage('Hello') {
            steps {
                echo 'Hello World'
            }
        }
        stage('SayHello') {
            steps {
                sayHello('Pepe')
            }
        }
        stage('AwsWorkflow') {
            steps {
                awsWorkflow() // throw CpsCompilationErrorsException: unable to resolve class io.github.io.github.mvillafuertem.aws.AwsWorkflow
            }
        }
    }
}
```

## slackSendNotification

![doc/slack-notification.png](doc/slack-notification.png)
