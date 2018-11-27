cd ./parent
call mvn clean deploy -Dmaven.test.skip=true
cd ../core
call mvn clean deploy -Dmaven.test.skip=true
cd ../codegen
call mvn clean deploy -Dmaven.test.skip=true
cd ../orm
call mvn clean deploy -Dmaven.test.skip=true
cd ../framework
call mvn clean deploy -Dmaven.test.skip=true
cd ../common/dpsdk
call mvn clean deploy -Dmaven.test.skip=true
cd ../..
