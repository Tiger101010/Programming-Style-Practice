for ((i = 1; i <= 2; ++i)) do
    echo ""
    echo "------------------------------------"
    echo "Task $i is runing..."
    echo "------------------------------------"
    echo ""

    if [[ $i == 1 ]]; then
        cd Twenty
        bash ./run.sh
        cd ..
    elif [[ $i == 2 ]]; then
        cd Twentysix
        echo "Compile and Run..."
        echo ""
        java -cp sqlite-jdbc-3.36.0.3.jar Twentysix.java ../../pride-and-prejudice.txt
    fi


done
