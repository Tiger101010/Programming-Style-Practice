echo "Compiling..."
javac JarClasses.java
echo "Running..."
java JarClasses ./json-20211205.jar
rm -f *.class