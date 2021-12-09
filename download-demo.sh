#!/bin/bash

set -e

if [[ ! -d demo ]] ; then
    mkdir demo
fi

cd demo

if [[ ! -e prometheus-2.30.1.linux-amd64.tar.gz ]] ; then
    curl -OL https://github.com/prometheus/prometheus/releases/download/v2.30.1/prometheus-2.30.1.linux-amd64.tar.gz
fi

if [[ ! -d prometheus-2.30.1.linux-amd64 ]] ; then
    tar xfz prometheus-2.30.1.linux-amd64.tar.gz
fi

if [[ ! -e grafana-8.1.5.linux-amd64.tar.gz ]] ; then
    curl -OL https://dl.grafana.com/oss/release/grafana-8.1.5.linux-amd64.tar.gz
fi

if [[ ! -d grafana-8.1.5 ]] ; then
    tar xfz grafana-8.1.5.linux-amd64.tar.gz
fi
