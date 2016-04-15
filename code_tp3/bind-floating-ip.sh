#!/bin/bash

source ./INF4410-08-projet-openrc.sh

nova floating-ip-create ext-net
nova floating-ip-list

echo "Enter the instance name :"

read instanceName

echo "Enter floating ip :"

read floatingIp

nova add-floating-ip $instanceName $floatingIp


