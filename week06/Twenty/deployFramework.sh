cd ./framework
javac *.java
jar cfm TFFramework.jar manifest.mf *.class
jar cf ITF.jar IWord.class IFreq.class
rm *.class
cp ./*.jar $(pwd)/../deploy