#!/usr/bin/env bash
MAX=9
for ((i = 1; i <= $MAX; ++i)) do
  echo "[$i] Run Exercise $i;"
done
echo "Please enter a number:"
read NUM
if ! [[ $NUM =~ ^[0-9]+$ ]] || [[ $NUM < 1 ]] || [[ $MAX < $NUM ]]; then
  echo "input should be 1, 2, ..., $MAX"
  exit
fi

run() {
  echo "------------------------------------"
  echo "Exercise ${NUM} is running..."
  echo "------------------------------------"
  echo ""
  cd "./week0$NUM"
  bash run.sh
}

if [[ $NUM < $MAX+1 ]]; then
  run
else
  echo "Haven't implemented"
fi