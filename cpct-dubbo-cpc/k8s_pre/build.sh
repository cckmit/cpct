#! /bin/bash
version=$1
project=bss-cpct-dubbo-cpc-pre
if [[ -n "$6" ]]; then
	project=$6
fi
hproject=$5
uharbor=$3
pharbor=$4
docker login -u$uharbor -p$pharbor $harbor
docker build --no-cache -t $project:$version ../
if [ $? -eq 0 ];then
    echo "build docker images success!"
else
    exit -1
fi
docker tag $project:${version} $harbor/$hproject/$project:${version}
docker push $harbor/$hproject/$project:${version}
if [ $? -eq 0 ];then
    echo "push docker images success!  ${project}:${version}"
else
    exit -1
fi
