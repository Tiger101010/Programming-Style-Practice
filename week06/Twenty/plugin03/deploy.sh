    pwd
    javac -cp ../framework/ITF.jar $(pwd)/*.java
    jar cf Freq1.jar -C $(pwd) *.class
    cp ./*.jar ../deploy
    rm *.jar