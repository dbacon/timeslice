
set -e
set -x

version="1.0.8"

rm -rf "$HOME"/.timeslice*
rm -rf tmp-deploy
mkdir tmp-deploy

cd tmp-deploy

unzip "../target/timeslice-host-${version}-dist.zip"

cd "timeslice-host-${version}"

bin/runme.sh -p 1080

