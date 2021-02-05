def call() {
    sh 'apt-get update'
    sh 'apt-get install -y python-lxml python-boto python-boto3'
    sh 'set STATICBUILD=true && pip install lxml boto boto3 ansible==2.6.2'
}
