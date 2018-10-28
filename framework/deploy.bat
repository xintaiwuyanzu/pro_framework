cd ./parent
call mvn clean install -Dmaven.test.skip=true
cd ../core
call mvn clean install -Dmaven.test.skip=true
cd ../codegen
call mvn clean install -Dmaven.test.skip=true
cd ../orm
call mvn clean install -Dmaven.test.skip=true
cd ../framework
call mvn clean install -Dmaven.test.skip=true
cd ..
