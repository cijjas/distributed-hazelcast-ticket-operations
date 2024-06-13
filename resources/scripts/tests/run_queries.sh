#!/bin/bash

# Configuración
ADDRESSES="192.168.0.233:5701"
CITY=("NYC" "CHI")
INPUT_PATH="/home/chris/Desktop/itba/2024C1/pod/2/large"
OUTPUT_PATH="../outputs"
REPEATS=5

mkdir -p $OUTPUT_PATH

execute_query() {
  query_num=$1
  additional_params=$2
  city=$3

  for ((i=1; i<=REPEATS; i++))
  do
    output_log="$OUTPUT_PATH/time${query_num}_${city}_${i}.txt"
    sh query${query_num}.sh -Daddresses=$ADDRESSES -Dcity=$city -DinPath=$INPUT_PATH -DoutPath=$OUTPUT_PATH $additional_params > $output_log
    echo "Query $query_num ejecutada para $city - Repetición $i"
  done
}

for city in "${CITY[@]}"
do
  execute_query 1 "" $city
  execute_query 2 "" $city
  execute_query 3 "-Dn=10" $city
  execute_query 4 "-Dfrom=01/01/1970 -Dto=31/12/2023" $city
  execute_query 5 "" $city
done

echo "Ejecuciones completadas."
