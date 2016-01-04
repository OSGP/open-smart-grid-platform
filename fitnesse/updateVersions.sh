#!/bin/bash

CONTENT_FILE=FitNesseRoot/content.txt
DEFINE_STRING="!define"
GENERIC_VERSION="OSGP_VERSION"

if [ ! -f $CONTENT_FILE ]; then
  echo "File $CONTENT_FILE does not exist, exiting"
  exit 1
fi

function getVersionFromPom
{
  POM=$1

  if [ ! -f $POM ]; then
    echo "POM $POM does not exist"
    exit 1
  fi

  #cat $POM | grep "<version>" | head -1 | sed "s#<[/]*version>##g;s# ##g"
  cat $POM | awk '  
  BEGIN { layer=0 }
  {
#    print $0
    if (layer == 1 && $0 ~ /version/ ) {
#      print $0
      if ( $0 ~ /^[	 ]*<version>.+<\/version>/ ) {
#        print "Version found "$0
        line = $0
        gsub("<[/]*version>", "", line)
        gsub(" ", "", line)
        gsub("	", "", line)
        print line
      }
    }
    if ( $0 ~ /^[ 	]*<[^\/!\?][^\>]+>[ ]*$/ || $0 ~ /^[ 	]*<project/ ) {
      layer++
#      print "Opening "layer
    }
    if ( $0 ~ /^[ 	]*<\/.+>[ ]*$/ ) {
      layer--
#      print "Closing "layer
    }
  }
  ' 
}

function AddOrReplaceVal
{
  VAR=$1
  VAL=$2
  echo "Var $VAR holds value $VAL"

  if [ `grep -c "^${DEFINE_STRING}[ ]*${VAR}[ ]*{" $CONTENT_FILE` -eq 0 ]; then
    echo "$VAR needs to be setup"
    echo "${DEFINE_STRING} ${VAR} {${VAL}}" >> $CONTENT_FILE
  else
    echo "$VAR will be replaced by actual value"
    cat $CONTENT_FILE | sed "s#\(${DEFINE_STRING}[ ]*${VAR}[ ]*{\).*#\1${VAL}}#g" > $CONTENT_FILE.new &&
    mv $CONTENT_FILE.new $CONTENT_FILE
  fi
}

SHARED_VAR="SHARED_VERSION"
#getVersionFromPom ../Shared/pom.xml
SHARED_VAL=`getVersionFromPom ../Shared/pom.xml`
AddOrReplaceVal $SHARED_VAR $SHARED_VAL

PLATFORM_VAR="PLATFORM_VERSION"
#getVersionFromPom ../Platform/pom.xml
PLATFORM_VAL=`getVersionFromPom ../Platform/pom.xml`
AddOrReplaceVal $PLATFORM_VAR $PLATFORM_VAL

PROTOCOL_ADAPTER_OSLP_VAR="PROTOCOL_ADAPTER_OSLP_VERSION"
#getVersionFromPom ../Protocol-Adapter-OSLP/pom.xml
PROTOCOL_ADAPTER_OSLP_VAL=`getVersionFromPom ../Protocol-Adapter-OSLP/pom.xml`
AddOrReplaceVal $PROTOCOL_ADAPTER_OSLP_VAR $PROTOCOL_ADAPTER_OSLP_VAL

echo ""
echo "Printing current values $CONTENT_FILE"
grep define $CONTENT_FILE | grep VERSION

