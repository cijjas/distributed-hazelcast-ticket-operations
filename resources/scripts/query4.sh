#!/bin/bash

CLIENT_JARS="../../client/target/tpe2-g7-client-2024.1Q/lib/jars/*"
TARGET_CLIENT="ar.edu.itba.pod.tpe2.client.query4.Q4Client"
TARGET_DIR="../../client/target/tpe2-g7-client-2024.1Q"

if [ ! -d "$TARGET_DIR" ]; then
    tar -xzf "../../client/target/tpe2-g7-client-2024.1Q-bin.tar.gz" -C "../../client/target/"
fi

java -cp "$CLIENT_JARS" "$TARGET_CLIENT" "$@"
