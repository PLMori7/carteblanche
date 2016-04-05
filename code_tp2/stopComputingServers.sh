#!/bin/bash

echo | fuser 5015/tcp | awk 2 | xargs kill &
echo | fuser 5016/tcp | awk 2 | xargs kill &
echo | fuser 5017/tcp | awk 2 | xargs kill &
#echo | fuser 5018/tcp | awk 2 | xargs kill &
#echo | fuser 5019/tcp | awk 2 | xargs kill 
