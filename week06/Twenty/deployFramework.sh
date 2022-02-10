cd /home/runner/SWE262P/week06/Twenty/framework
javac *.java
jar cfm TFFramework.jar manifest.mf *.class
jar cf ITF.jar IWord.class IFreq.class
cp ./*.jar $(pwd)/../deploy