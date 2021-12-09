#!/bin/bash

set -e

cd demo
rm -rf prometheus-2.30.1.linux-amd64
rm -rf grafana-8.1.5
tar xfz prometheus-2.30.1.linux-amd64.tar.gz
tar xfz grafana-8.1.5.linux-amd64.tar.gz
