    pwd
    javac -cp ../framework/ITF.jar $(pwd)/*.java
    jar cf Freq2.jar -C $(pwd) *.class
    cp ./*.jar ../deploy
    rm *.jar