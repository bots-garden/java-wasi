#!/bin/bash

cargo build --target=wasm32-wasi
cp target/wasm32-wasi/debug/hey_rust.wasm ../hey_rust.wasm

