for ((i = 1; i <= 3; ++i)) do
    echo ""
    echo "------------------------------------"
    echo "Task $i is runing..."
    echo "------------------------------------"
    echo ""

    if [[ $i == 1 ]]; then
        node Nine.js ../pride-and-prejudice.txt
    elif [[ $i == 2 ]]; then
        node Ten.js ../pride-and-prejudice.txt
    elif [[ $i == 3 ]]; then
        java Fifteen.java ../pride-and-prejudice.txt
    fi


done
