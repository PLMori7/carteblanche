#!/bin/bash

for i in `seq 1 7`;
do
	echo Number of bytes: 10^$i
	./client 54.153.69.164 $i
	echo
done
