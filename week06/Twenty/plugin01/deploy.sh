    pwd
    javac -cp ../deploy/ITF.jar $(pwd)/*.java
    jar cf Word1.jar -C $(pwd) *.class
    cp ./*.jar ../deploy
    rm *.jar