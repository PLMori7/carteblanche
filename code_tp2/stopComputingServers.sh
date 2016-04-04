#!/bin/bash

echo | fuser 5005/tcp | awk 2 | xargs kill &
echo | fuser 5006/tcp | awk 2 | xargs kill &
echo | fuser 5007/tcp | awk 2 | xargs kill &
echo | fuser 5008/tcp | awk 2 | xargs kill &
echo | fuser 5009/tcp | awk 2 | xargs kill 
