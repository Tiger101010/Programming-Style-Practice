MAX=6
for ((i = 4; i <= $MAX; ++i)) do
  echo "[$i] Run Task $i;"
done
echo "Please enter a number:"
read NUM
if ! [[ $NUM =~ ^[4-6]+$ ]] || [[ $NUM < 4 ]] || [[ $MAX < $NUM ]]; then
  echo "input should be 4, 5, 6"
  exit
fi

echo "------------------------------------"
echo "Task $NUM is runing..."
echo "It may take awhile..."
echo "------------------------------------"
echo ""


if [[ $NUM == 4 ]]; then
  FILENAME="Four.java"
  CLASSNAME="Four"
elif [[ $NUM == 5 ]]; then
  FILENAME="Five.java"
  CLASSNAME="Five"
elif [[ $NUM == 6 ]]; then
  FILENAME="Six.java"
  CLASSNAME="Six"
fi

javac $FILENAME
java $CLASSNAME ../pride-and-prejudice.txt
rm *.class