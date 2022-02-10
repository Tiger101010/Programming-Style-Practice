for ((i = 1; i <= 2; ++i)) do
    echo ""
    echo "------------------------------------"
    echo "Task $i is runing..."
    echo "------------------------------------"
    echo ""

    if [[ $i == 1 ]]; then
        cd Twenty
        bash ./run.sh
    elif [[ $i == 2 ]]; then
        echo "2"
    fi


done
