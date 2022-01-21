for ((i = 7; i <= 8; ++i)) do
    echo ""
    echo "------------------------------------"
    echo "Task $i is runing..."
    echo "It may take awhile..."
    echo "------------------------------------"
    echo ""

    if [[ $i == 7 ]]; then
        FILENAME="Seven.java"
    elif [[ $i == 8 ]]; then
        FILENAME="Eight.java"
    fi

    java -Xss512m $FILENAME ../pride-and-prejudice.txt
    echo ""
    echo "------------------------------------"
    echo "Task $i ends"
    echo "------------------------------------"
    echo ""

done
