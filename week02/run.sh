for ((i = 4; i <= 6; ++i)) do
    echo ""
    echo "------------------------------------"
    echo "Task $i is runing..."
    echo "It may take awhile..."
    echo "------------------------------------"
    echo ""

    if [[ $i == 4 ]]; then
        FILENAME="Four.java"
    elif [[ $i == 5 ]]; then
        FILENAME="Five.java"
    elif [[ $i == 6 ]]; then
        FILENAME="Six.java"
    fi

    java $FILENAME ../pride-and-prejudice.txt
    echo ""
    echo "------------------------------------"
    echo "Task $i ends"
    echo "------------------------------------"
    echo ""

done
