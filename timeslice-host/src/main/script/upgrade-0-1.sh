#! /bin/bash

java \
  -jar "${HOME}/.m2/repository/hsqldb/hsqldb/1.8.0.10/hsqldb-1.8.0.10.jar" \
    --inlineRc "url=jdbc:hsqldb:file:${HOME}/.timeslice-data/hsql/default-01,user=sa,password=" \
    "$1"
