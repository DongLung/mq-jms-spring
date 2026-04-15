#!/bin/bash

# Generate a Boot3-compatible source from the primary tree (JMS3/Boot4):
#

curdir=`pwd`
in="$1"
out="$2"

if [ -z "$in" -o -z "$out" ]
then
  echo "Usage: makeBoot3.sh inDir outDir"
  exit 1
fi
# echo "Copying files for Boot4 build from $in to $out"

if [ ! -d $in ]
then
  echo "Cannot find input directory $in"
  exit 1
fi

mkdir -p $out >/dev/null 2>&1
cd $in
# Create the structure
find . -type f |\
   grep -v bin/ |\
   cpio -upad $out
# And recopy the Java files doing any modifications as we go
find . -type f -name "*.java" |\
 grep -v bin/ |\
 while read f
do
   # Boot4 moved a bunch of imported classes that we have to swap back here
   cat $f |\
   sed "s/org.springframework.boot.jms.autoconfigure/org.springframework.boot.autoconfigure.jms/g" |\
   sed "s/org.springframework.boot.transaction.jta.autoconfigure/org.springframework.boot.autoconfigure.transaction.jta/g" > $out/$f
done

