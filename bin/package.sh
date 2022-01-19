#!/bin/bash
#
# Script to install package of MAS SCADA Historian Connector

set -x

mkdir -p buildimg/masshc/bin
mkdir -p buildimg/masshc/lib
cp bin/* buildimg/masshc/bin/.
cp target/*.jar buildimg/masshc/lib/.
cp target/dependencies/*.jar buildimg/masshc/lib/.
cp lib/* buildimg/masshc/lib/.
rm -f buildimg/masshc/lib/junit*
rm -f buildimg/masshc/bin/package.sh
cd buildimg
if [ -n "$TRAVIS_BUILD_DIR" ]; then
    tar -czf $TRAVIS_BUILD_DIR/mas-scada-historian-connector.tgz ./masshc
else
    tar -czf ./mas-scada-historian-connector.tgz ./masshc
fi

