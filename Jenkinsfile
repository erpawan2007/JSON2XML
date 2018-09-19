pipeline {
    agent {
        docker {
            image 'us.gcr.io/robomq-demo/quboid_a3.7:v1.6.0'
            args '-v $HOME/.m2:/root/.m2'
        }
    }
    stages {
        stage('Prepare') {
            steps {
                sh 'mkdir -p /opt/thingsConnect/config/json2xml \
					 && mkdir -p /opt/thingsConnect/lib/json2xml \
					 && mkdir -p /opt/thingsConnect/lib/common \
					 && mkdir -p /usr/local/bin/'
		sh "cp config/quboid.json /opt/thingsConnect/config/json2xml/"
		sh "cp config/log4j.properties /opt/thingsConnect/config/json2xml/"
            }
	    
        }
    }
}
