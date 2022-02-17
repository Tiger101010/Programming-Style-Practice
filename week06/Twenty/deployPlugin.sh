for ((i = 1; i <= 4; ++i)) do
  cd "./plugin0$i"
  bash ./deploy.sh
  rm *.class
  cd ..
done