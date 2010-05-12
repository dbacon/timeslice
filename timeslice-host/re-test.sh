
set -e
set -x

rm -rf "$HOME"/.timeslice*
rm -rf tmp-deploy
mkdir tmp-deploy

cd tmp-deploy

unzip ../target/timeslice-host-1.0.8-SNAPSHOT-dist.zip

cd timeslice-host-1.0.8-SNAPSHOT

bin/runme.sh -p 1080

