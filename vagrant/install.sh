#!/usr/bin/env bash
echo \#######################
echo installing packages
echo \#######################
sudo yum install java -y
sudo yum install wget -y
sudo yum groupinstall 'Development Tools' -y
echo \#######################
echo  packages installed
echo \#######################

cd /tmp
echo \#######################
echo   installing zookeeper
echo \#######################
wget -q http://www-eu.apache.org/dist/zookeeper/zookeeper-3.5.3-beta/zookeeper-3.5.3-beta.tar.gz
tar -xf zookeeper-3.5.3-beta.tar.gz
sudo mv zookeeper-3.5.3-beta /opt/zookeeper
cd /opt/zookeeper
mv conf/zoo_sample.cfg conf/zoo.cfg
bin/zkServer.sh start
echo \#######################
echo   zookeeper installed
echo \#######################

echo \#######################################################################################
echo \###'                                                                                 '\###
echo \###'                                  'PROVISION SUCCESSFUL'                           '\###
echo \###'                                                                                 '\###
echo \#######################################################################################



