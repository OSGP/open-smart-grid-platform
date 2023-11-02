#!/bin/bash

#echo "Merging the release to master often results during the merge command in a conflict where the Shared cannot be reset anymore because it exits. Please find a way to merge waithout submodules" && exit

function getNativePomList
{
  echo "function getNativePomList"
  Poms=$(find -name pom.xml)
# Excludes=$(cat .gitmodules | grep path | awk '{print $3}')
# Tested most with above, but found bug, below is new solution
  Excludes=$(git submodule | awk '{print $2}')
  echo "- exclude paths are: $Excludes"
  NativePoms=""
  
  for item in $Poms
  do 
    dir=$(echo $item | awk -F/ '{print $2}')
    if [ $(echo $Excludes | grep -c $dir) -eq 0 ]
    then 
      echo "DEBUG: $item will be included as native"
      NativePoms+="$item "
    fi
  done
}

function checkGithubConnection
{
  echo -n "* Checking connection to github"
  ping -w3 -q github.com >/dev/null
  CHECKCONNECTION=$?
  echo "...done"
  
  if [ $CHECKCONNECTION -ne 0 ]; then
    echo "* No connection to github detected, exiting" && exit 1
  else
    echo "* Connection to github is established, continuing"
  fi
}

function checkInput
{
  RELEASE=$1
  if [[ `echo $RELEASE | grep -c "^[0-9]\{1,2\}\.[0-9]\{1,2\}\.[0-9]\{1,2\}$"` -eq 0 ]]
  then
    echo "Release $RELEASE is not correct (A.B.C where A, B and C must be a number), exiting" 
    echo "Usage: $0 <release-number>" 
    echo "release-number: A.B.C where this fits a release branch"
    exit 1
  else
    echo "Release $RELEASE has a valid format, continuing"
  fi
}

function updateRepo
{
  git fetch origin
}

function updateGitmoduleFile
{
  GITMODFILE=.gitmodules
  echo "* Updating $GITMODFILE" 
  if [ -f $GITMODFILE ]; then
    sed -i "s#branch[ ]*=[ ]*release-$RELEASE#branch = master#g" $GITMODFILE &&
    git add $GITMODFILE &&
    echo "* Updated $GITMODFILE:"
    cat $GITMODFILE
  else
    echo "* No $GITMODFILE present, skipping"
  fi
}

function checkoutReleasebranch
{
  REL=$RELEASE
  if [ `git branch | grep -c release-$REL` -eq 0 ]; then
    git checkout -f release-$REL &&
    git pull origin release-$REL &&
    git reset --hard HEAD
    if [ $? -ne 0 ]; then
      echo "* Git checkout / pull was unsuccesfull" && exit 1
    else
      echo "* Git checkout / pull was succesfull"
    fi
  else
    echo "* Branch release-$REL already exists"
  fi
}

function resetSubmodule
{
  submodule=$1

  echo "* Reset submodule: ${submodule}"
  # First reset to get rid of unwanted change from release
  git reset HEAD ${submodule}  
}

function updateAllAvailableSubmodules
{  
  echo "* Resetting all available submodules"
  for submodulePath in $Excludes
  do
    [ -d $submodulePath ] && resetSubmodule $submodulePath
  done

  echo "* Updating all available submodules"
  
  echo "** Run git status and git submodule status"  
  git status
  git submodule status
  
  echo "** Run git submodule update --init --remote --recursive"  
  git submodule update --init --remote --recursive
  
  echo "** Run git status and git submodule status"  
  git status
  git submodule status
  
  echo "** Run git add -A"  
  git add -A
  git add .
  
  #echo "** Run git commit -am"  
  #git commit -am "Updates submodules."
  
  echo "** Run git status and git submodule status"  
  git status
  git submodule status
  
  echo "* Updated all available submodules"
}

function checkoutMaster
{
  echo "* Checkout master start" &&
  git checkout -f master &&
  echo "* Checkout master done" &&
  echo "* Pull start" &&
  git pull origin master &&
  echo "* Pull done"
}

function mergeToMaster
{
  echo "* Merge of release-$RELEASE without commit start" &&
  git merge release-$RELEASE -X theirs --no-commit
  echo "* Merge of release-$RELEASE without commit done" &&

  echo "* Update of submodules"
  updateGitmoduleFile &&
  getNativePomList &&
  updateAllAvailableSubmodules &&
  echo "* Update of submodules done"

  echo "* Merge release-$RELEASE commit start"
  git commit -m "Merging release-$RELEASE to master"
  echo "* Merge release-$RELEASE commit done"
  echo "* Push start"
  git push origin master &&
  echo "* Push done" &&
  if [ $? -ne 0 ]; then
    echo "Failed to merge to master, exiting" && exit 1
  fi
}

checkInput $1 &&
##checkGithubConnection &&
updateRepo &&
checkoutReleasebranch &&
checkoutMaster &&
mergeToMaster
echo "done"
